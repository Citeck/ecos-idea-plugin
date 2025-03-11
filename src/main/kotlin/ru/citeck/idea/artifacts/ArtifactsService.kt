package ru.citeck.idea.artifacts

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.components.Service
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiFile
import lombok.extern.slf4j.Slf4j
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
        @JvmStatic
        fun getInstance(): ArtifactsService {
            return ApplicationManager.getApplication().getService(ArtifactsService::class.java)
        }
    }

    private val artifacts: MutableMap<String, ArtifactTypeInfo> = ConcurrentHashMap()

    fun getArtifactTypeMeta(file: PsiFile?): ArtifactTypeMeta? {
        return getArtifactInfoForFile(file)?.meta
    }

    fun isActionAllowed(file: PsiFile?, actionId: String): Boolean {
        file ?: return false
        val info = getArtifactInfoForFile(file) ?: return false
        return !info.meta.disabledActions.contains(actionId)
    }

    fun getArtifactUrl(file: PsiFile): Promise<String> {

        val info = getNotNullArtifactInfoForFile(file)
        val artifactUrl = info.controller.getArtifactUrl(info.getRef(file))

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
        val atts = typeInfo.controller.prepareDeployAtts(file)

        if (!recordsApi.isExists(artifactRef).get()) {
            atts["id"] = artifactRef.getLocalId()
            artifactRef = artifactRef.withLocalId("")
        }

        recordsApi.mutate(artifactRef, atts)
    }

    fun fetchArtifact(file: PsiFile) {

        val typeInfo = getNotNullArtifactInfoForFile(file)

        val ref = typeInfo.getRef(file)

        val fetchAtts = mutableMapOf<String, String>()
        fetchAtts[NOT_EXISTS_ATT] = NOT_EXISTS_ATT
        fetchAtts.putAll(typeInfo.controller.getFetchAtts(file))

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
            typeInfo.controller.writeFetchedData(file, newContent)
        }
    }

    private fun getNotNullArtifactInfoForFile(file: PsiFile): ArtifactTypeInfo {
        return getArtifactInfoForFile(file) ?: error("File is not an artifact: ${file.virtualFile.path}")
    }

    private fun getArtifactInfoForFile(file: PsiFile?): ArtifactTypeInfo? {

        file ?: return null
        val module = ModuleUtil.findModuleForPsiElement(file) ?: return null
        val moduleInfo = CiteckProject.getInstance(file.project).getModuleInfo(module)

        var filePath = file.virtualFile.path
        val artifactsRoot = moduleInfo.type.artifactsRoot
        val artifactsRootIdx = filePath.indexOf(artifactsRoot)
        if (artifactsRootIdx == -1) {
            return null
        }
        filePath = filePath.substring(artifactsRootIdx + artifactsRoot.length)
        val typeIdDelimIdx = filePath.indexOf('/', filePath.indexOf('/', 1) + 1)
        if (typeIdDelimIdx == -1) {
            return null
        }
        return artifacts[filePath.substring(1, typeIdDelimIdx)]
    }

    fun getTemplates(artifactTypeId: String): List<FileTemplate> {
        return artifacts[artifactTypeId]?.template ?: error("Artifact '$artifactTypeId' not found")
    }

    fun register(artifactTypeMeta: ArtifactTypeMeta, template: List<FileTemplate>) {
        val controller: ArtifactTypeController = when (artifactTypeMeta.type) {
            ArtifactType.JSON, ArtifactType.YAML -> JsonYamlArtifactType()
            ArtifactType.BPMN -> ProcXmlArtifactType(ProcXmlArtifactType.SubType.BPMN)
            ArtifactType.DMN -> ProcXmlArtifactType(ProcXmlArtifactType.SubType.DMN)
            ArtifactType.NOTIFICATION_TEMPLATE -> NotificationTemplateType()
        }
        artifacts[artifactTypeMeta.id] = ArtifactTypeInfo(artifactTypeMeta, template, controller)
    }

    private fun runUndoTransparentAction(action: Runnable) {
        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction(action)
        }
    }

    private class ArtifactTypeInfo(
        val meta: ArtifactTypeMeta,
        val template: List<FileTemplate>,
        val controller: ArtifactTypeController
    ) {
        fun getRef(file: PsiFile): EntityRef {
            return EntityRef.create(meta.sourceId, controller.getArtifactId(file))
        }
    }
}
