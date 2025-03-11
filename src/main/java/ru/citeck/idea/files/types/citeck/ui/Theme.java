package ru.citeck.idea.files.types.citeck.ui;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.io.Compressor;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.filters.FileFilter;
import ru.citeck.idea.files.types.filters.FileNameFilter;
import ru.citeck.idea.files.types.filters.FilterAnd;

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
        return Optional.ofNullable(psiFile.getVirtualFile())
            .map(VirtualFile::getParent)
            .map(VirtualFile::getName)
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
    public boolean canFetch(PsiFile psiFile) {
        return false;
    }

}

