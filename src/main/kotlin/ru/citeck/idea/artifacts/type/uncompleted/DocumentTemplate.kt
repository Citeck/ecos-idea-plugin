package ru.citeck.idea.artifacts.type.uncompleted

/*import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.io.Compressor
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact
import ru.citeck.idea.files.types.filters.FileFilter
import ru.citeck.idea.files.types.filters.FolderNamePatternsFilter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.Deflater*/

//class DocumentTemplate : YamlEcosArtifact(PATH, SOURCE_ID) {
/*    private val filter: FileFilter = FolderNamePatternsFilter(PATH)

    override fun getIdPsiElement(psiFile: PsiFile): PsiElement? {
        return getMetaFile(psiFile)
            .map { psiFile: PsiFile? -> super.getIdPsiElement(psiFile) }
            .orElse(null)
    }

    private fun getMetaFile(psiFile: PsiFile): Optional<PsiFile?> {
        if (psiFile.name.endsWith(META_YML)) {
            return Optional.of(psiFile)
        }
        val psiDirectory = psiFile.parent ?: return Optional.empty()
        return Optional
            .of(psiFile.name)
            .map { input: String? ->
                TEMPLATE_NAME_PATTERN.matcher(
                    input
                )
            }
            .filter { obj: Matcher -> obj.find() }
            .map { obj: Matcher -> obj.group() }
            .map { tName: String -> psiDirectory.findFile(tName + META_YML) }
    }*/

    /*

    @Override
    public String getMimeType() {
        return "application/x-zip-compressed";
    }
*/
/*
    override fun getFilter(): FileFilter {
        return filter
    }
*/

    /*
    @Override
    public String getMutationAttribute() {
        return "content";
    }

    @Override
    public @Nullable Map<String, Object> getCustomMutationAttributes(PsiFile psiFile) {
        return Map.of("import?bool", true);
    }

    @Override
    public String getContentAttribute() {
        return "data";
    }
*/
/*    override fun getContent(psiFile: PsiFile): ByteArray {
        try {
            val psiDirectory = psiFile.parent ?: throw RuntimeException("Parent folder not found")

            val byteArrayOutputStream = ByteArrayOutputStream()

            val zip = Compressor.Zip(byteArrayOutputStream)
                .withLevel(Deflater.BEST_COMPRESSION)

            val pattern = getId(psiFile) + "(|_[\\w][\\w]).([\\w]*|meta.yml)"
            val bundle = Arrays
                .stream(psiDirectory.files)
                .filter { child: PsiFile -> child.name.matches(pattern.toRegex()) }
                .toList()

            for (file in bundle) {
                zip.addFile(file.name, File(file.virtualFile.path))
            }

            zip.close()

            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    override fun isIndexable(psiFile: PsiFile): Boolean {
        return psiFile.name.endsWith(META_YML)
    } *//*    @Override
    public void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception {
        if (psiFile.getParent() == null) {
            throw new RuntimeException("Unable to get parent directory");
        }
        CiteckZipContentUtil.applyBase64ZipContentToDirectory(psiFile.getParent(), content.asText());
    }*/
/*
    companion object {
        const val SOURCE_ID: String = "transformations/template"
        const val PATH: String = "/transformation/template/"
        const val META_YML: String = ".meta.yml"
        val TEMPLATE_NAME_PATTERN: Pattern = Pattern.compile("^[\\w_-]*(?=_[\\w][\\w]\\.[\\w]*)|^[\\w_-]*(?=\\.[\\w]*)")
    }
}*/
