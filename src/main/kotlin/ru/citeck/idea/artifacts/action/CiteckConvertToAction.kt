package ru.citeck.idea.artifacts.action

import com.intellij.json.psi.JsonFile
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.apache.commons.io.FilenameUtils
import org.jetbrains.yaml.psi.YAMLFile
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.json.Json
import ru.citeck.ecos.commons.json.YamlUtils
import ru.citeck.idea.artifacts.ArtifactTypeMeta
import ru.citeck.idea.utils.CiteckPsiUtils

open class CiteckConvertToAction(
    private val type: ConvertType
) : CiteckArtifactFileAction() {

    override fun perform(event: AnActionEvent) {

        val psiFile = getPsiFile(event) ?: return
        val parent = psiFile.parent ?: error("File parent is null. File name: ${psiFile.name}")

        val fileContent = when (psiFile) {
            is YAMLFile -> DataValue.of(YamlUtils.read(psiFile.text) ?: error("YAML File reading failed"))
            is JsonFile -> Json.mapper.readDataNotNull(psiFile.text)
            else -> error("Unsupported file type: ${psiFile.javaClass}")
        }
        val targetContent = when (type) {
            ConvertType.JSON_TO_YAML -> YamlUtils.toString(fileContent)
            ConvertType.YAML_TO_JSON -> Json.mapper.toPrettyStringNotNull(fileContent)
        }

        val nameWithoutExt = FilenameUtils.getBaseName(psiFile.name)

        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().runUndoTransparentAction {
                val targetFile = parent.createFile(nameWithoutExt + "." + type.toExt)
                CiteckPsiUtils.setContent(targetFile, targetContent)
                psiFile.delete()
            }
        }
    }

    override fun isActionAllowed(
        event: AnActionEvent,
        file: PsiFile,
        project: Project,
        typeMeta: ArtifactTypeMeta
    ): Boolean {
        return type.fromType.isInstance(file)
    }

    override fun getArtifactActionId(): String {
        return "convert-" + type.name.lowercase().replace('_', '-')
    }

    enum class ConvertType(
        val fromType: Class<out PsiFile>,
        val toExt: String
    ) {
        YAML_TO_JSON(YAMLFile::class.java, "json"),
        JSON_TO_YAML(JsonFile::class.java, "yml")
    }

    class YamlToJson : CiteckConvertToAction(ConvertType.YAML_TO_JSON)

    class JsonToYaml : CiteckConvertToAction(ConvertType.JSON_TO_YAML)
}
