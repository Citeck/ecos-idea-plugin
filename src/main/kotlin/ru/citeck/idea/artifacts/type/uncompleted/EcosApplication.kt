package ru.citeck.idea.artifacts.type.uncompleted
/*

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTagValue
import com.intellij.util.io.Compressor
import ecos.com.fasterxml.jackson210.databind.ObjectMapper
import lombok.Data
import ru.citeck.ecos.commons.json.Json.mapper
import ru.citeck.ecos.commons.json.YamlUtils.read
import ru.citeck.idea.files.types.citeck.EcosArtifact
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact
import ru.citeck.idea.files.types.filters.FileFilter
import ru.citeck.idea.files.types.filters.FileNameFilter
import ru.citeck.idea.files.types.filters.FilterAnd
import ru.citeck.idea.files.types.filters.FilterOr
import ru.citeck.idea.utils.MavenUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.*
import java.util.function.Function
import java.util.zip.Deflater

interface EcosApplication : EcosArtifact {
    override fun getContent(psiFile: PsiFile): ByteArray {
        try {
            val meta = getApplicationMeta(psiFile) ?: throw RuntimeException("Unable to read application meta")

            val applicationVersion = getApplicationVersion(psiFile)
                ?: throw RuntimeException("Unable to read application version")

            meta.version = applicationVersion
            val metaPath = psiFile.virtualFile.toNioPath()

            val appDirectory = Optional
                .ofNullable<PsiDirectory>(psiFile.parent)
                .map<VirtualFile> { obj: PsiDirectory -> obj.virtualFile }
                .map<String> { obj: VirtualFile -> obj.path }
                .map<File>(Function<String, File> { pathname: String? -> File(pathname) })
                .orElseThrow<RuntimeException> { RuntimeException("Unable to determine application path") }

            val byteArrayOutputStream = ByteArrayOutputStream()

            val zip = Compressor.Zip(byteArrayOutputStream)
                .withLevel(Deflater.BEST_COMPRESSION)

            zip.filter { fileName: String?, path: Path? -> metaPath != path }
            zip.addDirectory(appDirectory)
            zip.addFile("meta.json", ObjectMapper().writeValueAsString(meta).toByteArray(StandardCharsets.UTF_8))
            zip.close()

            return byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getApplicationMeta(psiFile: PsiFile): Meta? {
        try {
            val data = read(psiFile.text) ?: return null
            return mapper.convertNotNull(data, Meta::class.java)
        } catch (e: Exception) {
            log.error("Application meta reading failed: " + psiFile.virtualFile.path, e)
            return null
        }
    }

    fun getApplicationVersion(psiFile: PsiFile?): String? {
        return Optional
            .ofNullable(MavenUtils.getPomFile(psiFile))
            .map { obj: XmlFile -> obj.rootTag }
            .map { xmlTag: XmlTag? -> xmlTag!!.findFirstSubTag("version") }
            .map { obj: XmlTag? -> obj!!.value }
            .map { obj: XmlTagValue -> obj.text }
            .orElse(null)
    }

    @Data
    class Meta {
        private val id: String? = null
        private val name: Any? = null
        internal var version: String? = null
    }

    class JSON : JsonEcosArtifact(PATH, SOURCE_ID), EcosApplication {
        private val filter: FileFilter =
            FilterAnd(super.getFilter(), FileNameFilter(PATH + "meta.json"))

        override fun getFilter(): FileFilter {
            return filter
        }
    }

    class YAML : YamlEcosArtifact(PATH, SOURCE_ID), EcosApplication {
        private val filter: FileFilter = FilterAnd(
            super.getFilter(), FilterOr(
                FileNameFilter(PATH + "meta.yml"),
                FileNameFilter(PATH + "meta.yaml")
            )
        )

        override fun getFilter(): FileFilter {
            return filter
        }
    }

    companion object {
        val log: Logger = Logger.getInstance(
            EcosApplication::class.java
        )

        const val SOURCE_ID: String = "eapps/ecosapp"
        const val PATH: String = "/src/main/resources/app/"
    }
}
*/
