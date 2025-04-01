package ru.citeck.idea.artifacts.type.uncompleted

//class Theme : JsonEcosArtifact(PATH, SOURCE_ID) {
/*    private val filter: FileFilter =
        FilterAnd(super.getFilter(), FileNameFilter("meta.json"))*/

    /*    @Override
   public String getContentAttribute() {
       return "data";
   }

   @Override
   public String getMutationAttribute() {
       return "_content";
   }*/
/*    override fun getFilter(): FileFilter {
        return filter
    }

    override fun getIdPsiElement(psiFile: PsiFile): PsiElement {
        return psiFile
    }

    override fun getId(psiFile: PsiFile): String? {
        return Optional.ofNullable(psiFile.virtualFile)
            .map { obj: VirtualFile -> obj.parent }
            .map { obj: VirtualFile -> obj.name }
            .orElse(null)
    }

    override fun getContent(psiFile: PsiFile): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val zip = Compressor.Zip(byteArrayOutputStream)
            .withLevel(Deflater.BEST_COMPRESSION)

        val psiDirectory = psiFile.parent ?: throw RuntimeException("Unable to get theme folder")

        try {
            zip.addDirectory(getId(psiFile)!!, File(psiDirectory.virtualFile.path))
            zip.close()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        return byteArrayOutputStream.toByteArray()
    }*/ /*    @Override
    public boolean canFetch(PsiFile psiFile) {
        return false;
    }*/

/*    companion object {
        var SOURCE_ID: String = "uiserv/theme"
        var PATH: String = "/ui/theme/"
    }
}*/

