package ru.citeck.idea.artifacts

import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.Constraints
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.PluginId
import org.apache.commons.lang3.StringUtils
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.json.Json.mapper
import ru.citeck.idea.CiteckPlugin
import java.nio.charset.StandardCharsets
import java.util.function.Consumer

@Service
class ArtifactsMetaLoader : Disposable {

    companion object {
        private const val NEW_ARTIFACT_ACTION_ID_PREFIX = "citeck-create-artifact-"
        private const val ARTIFACTS_META_ROOT = "/citeck/artifacts/"

        private const val CREATE_NEW_ACTIONS_GROUP_ID = "NewGroup"

        private val log = Logger.getInstance(ArtifactsMetaLoader::class.java)
    }

    private var newArtifactsGroup: CreateArtifactsGroup? = null

    init {
        log.info("Begin artifacts actions registration")
        try {
            val artifactsList: List<String> = javaClass.getResourceAsStream("/citeck/artifacts/index.json").use { input ->
                if (input != null) {
                    mapper.readNotNull(input.readAllBytes(), DataValue::class.java).asStrList()
                } else {
                    emptyList()
                }
            }
            loadTypesMeta(artifactsList);
        } catch (e: Exception) {
            log.error("Artifacts index can't be loaded", e)
        }
    }

    private fun loadTypesMeta(artifactsList: List<String>) {

        log.info("Artifacts index: " + mapper.toString(artifactsList))

        val actionManager = ActionManager.getInstance()

        val newArtifactsGroup = CreateArtifactsGroup()
        this.newArtifactsGroup = newArtifactsGroup

        (actionManager.getAction(CREATE_NEW_ACTIONS_GROUP_ID) as DefaultActionGroup)
            .add(newArtifactsGroup, Constraints.FIRST)

        val groups: MutableMap<String, DefaultActionGroup> = HashMap()

        val artifactsService = ArtifactsService.getInstance()

        for (artifactMetaPath in artifactsList) {
            val meta = readArtifactMeta(artifactMetaPath) ?: continue
            val groupId = meta.typeId.substring(0, meta.typeId.indexOf('/'))
            val actionGroup = groups.computeIfAbsent(groupId) { id: String ->
                val groupMeta = readArtifactsGroupMeta(id)
                val newGroup = DefaultActionGroup(
                    groupMeta.name, "", null
                )
                newGroup.isPopup = true
                newArtifactsGroup.add(newGroup, Constraints.LAST)
                newGroup
            }

            val actionId = NEW_ARTIFACT_ACTION_ID_PREFIX + meta.typeId.replace("/", "_")

            val action = CreateArtifactAction.Builder()
                .withText(meta.name)
                .withMeta(meta)
                .withDescription(StringUtils.defaultIfBlank(meta.description, null))
                .build()

            actionManager.registerAction(actionId, action, PluginId.getId(CiteckPlugin.ID))
            actionGroup.add(action, Constraints.LAST)
            artifactsService.register(meta, readTemplate(meta, artifactMetaPath))
        }
    }

    private fun readArtifactsGroupMeta(groupId: String): ArtifactsGroupMeta {
        var meta = readFile(
            "/citeck/artifacts/$groupId/group.json",
            ArtifactsGroupMeta::class.java
        )
        if (meta == null) {
            meta = ArtifactsGroupMeta()
            meta.name = groupId
        }
        return meta
    }

    private fun readTemplate(meta: ArtifactTypeMeta, metaPath: String): List<FileTemplate> {
        val basePath = metaPath.substring(0, metaPath.lastIndexOf('/') + 1)
        val templateNames = when (meta.kind) {
            ArtifactKind.JSON -> listOf("template.json.ft")
            ArtifactKind.YAML -> listOf("template.yml.ft")
            ArtifactKind.BPMN -> listOf("template.bpmn.xml.ft")
            ArtifactKind.DMN -> listOf("template.dmn.xml.ft")
            ArtifactKind.NOTIFICATION_TEMPLATE -> listOf(
                "template.html_en.ftl.ft",
                "template.html.meta.yml.ft"
            )
        }
        return templateNames.mapNotNull { templateFileName ->
            val templateFile = readTextFile(ARTIFACTS_META_ROOT + basePath + templateFileName) ?: ""
            if (templateFile.isNotBlank()) {
                val extension = templateFileName.substringAfter('.').substringBeforeLast('.')
                val template = CustomFileTemplate("template", extension)
                template.text = templateFile
                template
            } else {
                null
            }
        }
    }

    private fun readArtifactMeta(path: String): ArtifactTypeMeta? {
        val metaRaw = readJsonFile(ARTIFACTS_META_ROOT + path)
        if (metaRaw.isEmpty()) {
            return null
        }
        val meta = ArtifactTypeMeta.create()
        mapper.applyData(meta, metaRaw)

        meta.withTypeId(path.substring(0, path.lastIndexOf("/")))

        if (StringUtils.isBlank(meta.name)) {
            meta.withName(meta.typeId)
        }
        return meta.build()
    }

    private fun readTextFile(path: String): String? {
        try {
            javaClass.getResourceAsStream(path).use { input ->
                if (input == null) {
                    return null
                }
                return String(input.readAllBytes(), StandardCharsets.UTF_8)
            }
        } catch (e: Exception) {
            log.error("Artifact meta reading failed: $path", e)
            return null
        }
    }

    private fun <T : Any> readFile(path: String, type: Class<T>): T? {
        return readJsonFile(path).getAs(type)
    }

    private fun readJsonFile(path: String): DataValue {
        return try {
            javaClass.getResourceAsStream(path).use { input ->
                input?.let { mapper.readData(it.readAllBytes()) }
            }
        } catch (e: Exception) {
            log.error("Artifact meta reading failed: $path", e)
            null
        } ?: DataValue.NULL
    }

    override fun dispose() {
        val actionManager = ActionManager.getInstance()
        actionManager.getActionIdList(NEW_ARTIFACT_ACTION_ID_PREFIX).forEach(Consumer { actionId: String? ->
            actionManager.unregisterAction(
                actionId!!
            )
        })
        newArtifactsGroup?.let {
            (actionManager.getAction(CREATE_NEW_ACTIONS_GROUP_ID) as DefaultActionGroup).remove(it)
        }
    }

    class ArtifactsGroupMeta(
        var name: String = ""
    )
}
