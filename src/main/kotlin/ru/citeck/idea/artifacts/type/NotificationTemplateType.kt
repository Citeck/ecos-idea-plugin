package ru.citeck.idea.artifacts.type

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.util.io.Compressor
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.idea.utils.CiteckZipContentUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import java.util.zip.Deflater

class NotificationTemplateType : ArtifactTypeController {

    companion object {
        private const val META_YML_SUFFIX = ".meta.yml"
        private val TEMPLATE_FILE_NAME_REGEX = "(.+)_\\w+\\.ftl".toRegex(RegexOption.IGNORE_CASE)
        private val TEMPLATE_FILES_SUFFIX_REGEX = "(\\.\\w+_\\w+\\.ftl|\\.meta\\.yml)".toRegex()
    }

    private val yamlType = JsonYamlArtifactType()

    override fun prepareDeployAtts(file: PsiFile): ObjectData {
        val vFile = file.virtualFile
        return ObjectData.create().set("_content", DataValue.createArr().add(
            DataValue.createObj()
                .set("storage", "base64")
                .set("type", "application/zip")
                .set("name", vFile.name)
                .set("originalName", vFile.name)
                .set("url", "data:application/zip;base64," +
                    Base64.getEncoder().encodeToString(getTemplateContent(file)))
        ))
    }

    override fun getArtifactId(file: PsiFile): String {
        val metaFile = getMetaFile(file) ?: return ""
        return yamlType.getArtifactId(metaFile)
    }

    override fun getFetchAtts(file: PsiFile): Map<String, String> {
        return mapOf("content" to "data?str")
    }

    override fun writeFetchedData(file: PsiFile, value: ObjectData) {
        val parent = file.parent ?: error("Unable to get parent directory")
        CiteckZipContentUtil.applyBase64ZipContentToDirectory(parent, value["content"].asText())
    }

    private fun getTemplateContent(file: PsiFile): ByteArray {

        val templateDir: PsiDirectory = file.parent ?: error("Parent folder not found")

        val byteArrayOutputStream = ByteArrayOutputStream()

        val zip = Compressor.Zip(byteArrayOutputStream)
            .withLevel(Deflater.BEST_COMPRESSION)

        val artifactId = getArtifactId(file)
        for (child in templateDir.files) {
            val name = child.name
            if (!name.startsWith(artifactId)
                    || !TEMPLATE_FILES_SUFFIX_REGEX.matches(name.substring(artifactId.length))) {
                continue
            }
            zip.addFile(file.name, File(file.virtualFile.path))
        }

        zip.close()

        return byteArrayOutputStream.toByteArray()
    }

    private fun getMetaFile(psiFile: PsiFile): PsiFile? {
        val name = psiFile.name
        if (name.endsWith(META_YML_SUFFIX)) {
            return psiFile
        }
        val psiDirectory = psiFile.parent ?: return null
        val matchResult = TEMPLATE_FILE_NAME_REGEX.matchEntire(name) ?: return null
        val baseName = matchResult.groups[1]?.value ?: return null
        return psiDirectory.findFile(baseName + META_YML_SUFFIX)
    }
}
