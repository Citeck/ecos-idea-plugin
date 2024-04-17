package ru.citeck.ecos.files.types.ecos.ui;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.io.Compressor;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FileNameFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Optional;
import java.util.zip.Deflater;

public class Theme extends JsonEcosArtifact {

    public static String SOURCE_ID = "uiserv/theme";
    public static String PATH = "/ui/theme/";

    private final FileFilter filter;

    public Theme() {
        super(PATH, SOURCE_ID);
        this.filter = new FilterAnd(super.getFilter(), new FileNameFilter("meta.json"));
    }

    @Override
    public String getContentAttribute() {
        return "data";
    }

    @Override
    public String getMutationAttribute() {
        return "_content";
    }

    @Override
    public FileFilter getFilter() {
        return filter;
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return psiFile;
    }

    @Override
    public String getId(PsiFile psiFile) {
        return Optional
                .ofNullable(psiFile.getParent())
                .map(PsiDirectory::getName)
                .orElse(null);
    }

    @Override
    public byte[] getContent(PsiFile psiFile) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Compressor.Zip zip = new Compressor
                .Zip(byteArrayOutputStream)
                .withLevel(Deflater.BEST_COMPRESSION);

        PsiDirectory psiDirectory = psiFile.getParent();
        if (psiDirectory == null) {
            throw new RuntimeException("Unable to get theme folder");
        }

        try {
            zip.addDirectory(getId(psiFile), new File(psiDirectory.getVirtualFile().getPath()));
            zip.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/develop/settings_kb/interface/%D0%A2%D0%B5%D0%BC%D1%8B_%D0%B8%D0%BD%D1%82%D0%B5%D1%80%D1%84%D0%B5%D0%B9%D1%81%D0%B0.html";
    }

    @Override
    public boolean canFetch(PsiFile psiFile) {
        return false;
    }

}

