package ru.citeck.idea.files.types.citeck.transformation;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.io.Compressor;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;
import ru.citeck.idea.files.types.filters.FileFilter;
import ru.citeck.idea.files.types.filters.FolderNamePatternsFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;

public class DocumentTemplate extends YamlEcosArtifact {

    public static final String SOURCE_ID = "transformations/template";
    public static final String PATH = "/transformation/template/";
    public static final String META_YML = ".meta.yml";
    public static final Pattern TEMPLATE_NAME_PATTERN =
        Pattern.compile("^[\\w_-]*(?=_[\\w][\\w]\\.[\\w]*)|^[\\w_-]*(?=\\.[\\w]*)");


    private final FileFilter filter = new FolderNamePatternsFilter(PATH);

    public DocumentTemplate() {
        super(PATH, SOURCE_ID);
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return getMetaFile(psiFile)
            .map(super::getIdPsiElement)
            .orElse(null);
    }

    private Optional<PsiFile> getMetaFile(PsiFile psiFile) {
        if (psiFile.getName().endsWith(META_YML)) {
            return Optional.of(psiFile);
        }
        PsiDirectory psiDirectory = psiFile.getParent();
        if (psiDirectory == null) {
            return Optional.empty();
        }
        return Optional
            .of(psiFile.getName())
            .map(TEMPLATE_NAME_PATTERN::matcher)
            .filter(Matcher::find)
            .map(Matcher::group)
            .map(tName -> psiDirectory.findFile(tName + META_YML));
    }
/*

    @Override
    public String getMimeType() {
        return "application/x-zip-compressed";
    }
*/

    @Override
    public FileFilter getFilter() {
        return filter;
    }

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

    @Override
    public byte[] getContent(PsiFile psiFile) {
        try {

            PsiDirectory psiDirectory = psiFile.getParent();
            if (psiDirectory == null) {
                throw new RuntimeException("Parent folder not found");
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            Compressor.Zip zip = new Compressor
                .Zip(byteArrayOutputStream)
                .withLevel(Deflater.BEST_COMPRESSION);

            String pattern = getId(psiFile) + "(|_[\\w][\\w]).([\\w]*|meta.yml)";
            List<PsiFile> bundle = Arrays
                .stream(psiDirectory.getFiles())
                .filter(child -> child.getName().matches(pattern))
                .toList();

            for (PsiFile file : bundle) {
                zip.addFile(file.getName(), new File(file.getVirtualFile().getPath()));
            }

            zip.close();

            return byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean isIndexable(PsiFile psiFile) {
        return psiFile.getName().endsWith(META_YML);
    }

/*    @Override
    public void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception {
        if (psiFile.getParent() == null) {
            throw new RuntimeException("Unable to get parent directory");
        }
        CiteckZipContentUtil.applyBase64ZipContentToDirectory(psiFile.getParent(), content.asText());
    }*/

}
