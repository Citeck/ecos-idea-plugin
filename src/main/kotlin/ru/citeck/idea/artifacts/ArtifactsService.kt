package ru.citeck.idea.artifacts

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import lombok.extern.slf4j.Slf4j
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import ru.citeck.ecos.webapp.api.entity.EntityRef
import ru.citeck.ecos.webapp.api.promise.Promise
import ru.citeck.idea.artifacts.type.ArtifactTypeController
import ru.citeck.idea.artifacts.type.JsonYamlArtifactType
import ru.citeck.idea.artifacts.type.NotificationTemplateType
import ru.citeck.idea.artifacts.type.ProcXmlArtifactType
import ru.citeck.idea.project.CiteckProject
import ru.citeck.idea.records.ServerRecordsApi
import ru.citeck.idea.settings.servers.CiteckServerSelector
import java.util.concurrent.ConcurrentHashMap

@Slf4j
@Service
class ArtifactsService {

    companion object {
        private const val NOT_EXISTS_ATT = "_notExists?bool!"
        // Minimal type prefix: /a/b/
        private const val MINIMAL_TYPE_PATH_LENGTH = 5

        private val log = Logger.getInstance(ArtifactsService::class.java)

        @JvmStatic
        fun getInstance(): ArtifactsService {
            return ApplicationManager.getApplication().getService(ArtifactsService::class.java)
        }
    }

    private val artifactTypes: MutableMap<String, ArtifactTypeInfo> = ConcurrentHashMap()

    fun getArtifactTypes(): Set<String> {
        return artifactTypes.keys
    }

    fun getArtifactTypeMeta(file: PsiFile?): ArtifactTypeMeta? {
        return getArtifactInfoForFile(file)?.getMeta()
    }

    fun getArtifactInfo(file: VirtualFile, project: Project): ArtifactInfo? {
        return getArtifactInfoForFile(file, project)
    }

    fun getArtifactInfo(file: PsiFile): ArtifactInfo? {
        return getArtifactInfoForFile(file)
    }

    fun isActionAllowed(file: PsiFile?, actionId: String): Boolean {
        file ?: return false
        val info = getArtifactInfoForFile(file) ?: return false
        return !info.getMeta().disabledActions.contains(actionId)
    }

    fun getArtifactUrl(file: PsiFile): Promise<String> {

        val info = getNotNullArtifactInfoForFile(file)
        val artifactUrl = info.getController().getArtifactUrl(info.getRef(file))

        return CiteckServerSelector.getServer(file.project).then { it.host + artifactUrl }
    }

    fun getArtifactRef(file: PsiFile): EntityRef {
        return getNotNullArtifactInfoForFile(file).getRef(file)
    }

    fun deployArtifact(file: PsiFile) {

        val typeInfo = getNotNullArtifactInfoForFile(file)
        var artifactRef = typeInfo.getRef(file)
        require(artifactRef.getLocalId().isNotBlank()) { "Artifact id is blank" }

        val recordsApi = ServerRecordsApi.getInstance(file.project)
        val atts = typeInfo.getController().prepareDeployAtts(file)

        if (!recordsApi.isExists(artifactRef).get()) {
            atts["id"] = artifactRef.getLocalId()
            artifactRef = artifactRef.withLocalId("")
        }

        recordsApi.mutate(artifactRef, atts)
    }

    fun fetchArtifact(file: PsiFile) {

        val artifactInfo = getNotNullArtifactInfoForFile(file)

        val ref = artifactInfo.getRef(file)

        val fetchAtts = mutableMapOf<String, String>()
        fetchAtts[NOT_EXISTS_ATT] = NOT_EXISTS_ATT
        fetchAtts.putAll(artifactInfo.getController().getFetchAtts(file))

        val newContent = ServerRecordsApi.getInstance(file.project)
            .loadAtts(ref, fetchAtts)
            .get()

        if (newContent.isEmpty()) {
            throw RuntimeException("Artifact content is empty")
        }
        if (newContent[NOT_EXISTS_ATT].asBoolean()) {
            throw RuntimeException("Artifact '${ref.getLocalId()}' does not exist on the server")
        }
        runUndoTransparentAction {
            artifactInfo.getController().writeFetchedData(file, newContent)
        }
    }

    private fun getNotNullArtifactInfoForFile(file: PsiFile): ArtifactInfo {
        return getArtifactInfoForFile(file.virtualFile, file.project)
            ?: error("File is not an artifact: ${file.virtualFile?.path}")
    }

    private fun getNotNullArtifactInfoForFile(file: VirtualFile, project: Project): ArtifactInfo {
        return getArtifactInfoForFile(file, project)
            ?: error("File is not an artifact: ${file.path}")
    }

    private fun getArtifactInfoForFile(file: PsiFile?): ArtifactInfo? {
        file ?: return null
        return getArtifactInfoForFile(file.virtualFile, file.project)
    }

    private fun getArtifactInfoForFile(file: VirtualFile?, project: Project?): ArtifactInfo? {

        file ?: return null
        project ?: return null

        val module = ModuleUtil.findModuleForFile(file, project) ?: return null
        val moduleInfo = CiteckProject.getInstance(project).getModuleInfo(module)
        if (moduleInfo.isNone()) {
            return null
        }

        var filePath = file.path
        val artifactsRoot = moduleInfo.type.artifactsRoot
        val artifactsRootIdx = filePath.indexOf(artifactsRoot)
        if (artifactsRootIdx == -1) {
            return null
        }
        filePath = filePath.substring(artifactsRootIdx + artifactsRoot.length)
        val typeIdDelimIdx = filePath.indexOf('/', filePath.indexOf('/', 1) + 1)
        if (typeIdDelimIdx < MINIMAL_TYPE_PATH_LENGTH) {
            return null
        }

        val typeInfo = artifactTypes[filePath.substring(1, typeIdDelimIdx)] ?: return null

        val psiFile = ReadAction.compute<PsiFile?, Throwable> { file.toPsiFile(project) } ?: return null

        return ArtifactInfoImpl(psiFile, typeInfo)
    }

    fun getTemplates(artifactTypeId: String): List<FileTemplate> {
        return artifactTypes[artifactTypeId]?.template ?: error("Artifact '$artifactTypeId' not found")
    }

    fun register(artifactTypeMeta: ArtifactTypeMeta, template: List<FileTemplate>) {

        fun printInvalidParamsMsg() {
            log.error("Invalid params for type " + artifactTypeMeta.typeId + ". Artifact type won't be registered")
        }

        val controller: ArtifactTypeController = when (artifactTypeMeta.kind) {
            ArtifactKind.JSON, ArtifactKind.YAML -> {
                val params = artifactTypeMeta.controller.getAs(JsonYamlArtifactType.Params::class.java)
                if (params == null) {
                    printInvalidParamsMsg()
                    return
                }
                JsonYamlArtifactType(params)
            }
            ArtifactKind.BPMN -> ProcXmlArtifactType(ProcXmlArtifactType.SubType.BPMN)
            ArtifactKind.DMN -> ProcXmlArtifactType(ProcXmlArtifactType.SubType.DMN)
            ArtifactKind.NOTIFICATION_TEMPLATE -> NotificationTemplateType()
        }
        artifactTypes[artifactTypeMeta.typeId] = ArtifactTypeInfo(artifactTypeMeta, template, controller)
    }

    private fun runUndoTransparentAction(action: Runnable) {
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction(action)
        }
    }

    private class ArtifactInfoImpl(
        val file: PsiFile,
        val typeInfo: ArtifactTypeInfo
    ) : ArtifactInfo {

        private val idPsiElement by lazy {
            typeInfo.controller.getArtifactIdPsiElement(file)
        }

        override fun getTypeId(): String {
            return typeInfo.meta.typeId
        }

        override fun getArtifactIdPsiElement(): PsiElement? {
            return idPsiElement
        }

        override fun getArtifactId(): String {
            val idElement = getArtifactIdPsiElement()
            return JsonPsiUtil.stripQuotes(idElement?.text ?: "")
        }

        override fun getRef(file: PsiFile): EntityRef {
            return EntityRef.create(typeInfo.meta.sourceId, getArtifactId())
        }

        override fun getController(): ArtifactTypeController {
            return typeInfo.controller
        }

        override fun getMeta(): ArtifactTypeMeta {
            return typeInfo.meta
        }
    }

    private class ArtifactTypeInfo(
        val meta: ArtifactTypeMeta,
        val template: List<FileTemplate>,
        val controller: ArtifactTypeController
    )
}
