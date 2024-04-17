package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.io.Compressor;
import com.intellij.util.io.Decompressor;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.files.types.filters.FolderNamePatternsFilter;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.zip.Deflater;

public class NotificationTemplate extends YamlEcosArtifact {

    public static final String SOURCE_ID = "notifications/template";
    public static final String PATH = "/notification/template/";
    public static final String HTML_FTL_META_YML = ".html.ftl.meta.yml";
    public static final String FILE_NAME_PATTERN = "[\\w-_]*.html(|_[\\w][\\w]).ftl(|.meta.yml)";

    private final FileFilter filter = new FilterAnd(
            new FolderNamePatternsFilter(PATH),
            (file, project) -> file.getName().matches(FILE_NAME_PATTERN)
    );

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return getMetaFile(psiFile)
                .map(super::getIdPsiElement)
                .orElse(null);
    }

    private Optional<PsiFile> getMetaFile(PsiFile psiFile) {
        if (psiFile.getName().endsWith(HTML_FTL_META_YML)) {
            return Optional.of(psiFile);
        }
        PsiDirectory psiDirectory = psiFile.getParent();
        if (psiDirectory == null) {
            return Optional.empty();
        }
        return Optional
                .of(psiFile.getName())
                .map(fileName -> fileName.split("\\."))
                .filter(fileNameParts -> fileNameParts.length > 0)
                .map(parts -> parts[0] + HTML_FTL_META_YML)
                .map(psiDirectory::findFile);
    }

    public NotificationTemplate() {
        super(PATH, SOURCE_ID);
    }

    @Override
    public String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/notifications/notifications_template.html";
    }

    @Override
    public FileFilter getFilter() {
        return filter;
    }

    @Override
    public String getMutationAttribute() {
        return "_content";
    }

    @Override
    public String getContentAttribute() {
        return "data";
    }

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

            String pattern = getId(psiFile) + ".html(|_[\\w][\\w]).ftl(|.meta.yml)";
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
        return psiFile.getName().endsWith(HTML_FTL_META_YML);
    }

    @Override
    public void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception {
        File tmpDir = FileUtil.createTempDirectory("ecos-content", "tmp");
        tmpDir.deleteOnExit();

        File contentZip = new File(tmpDir, "content.zip");

        FileOutputStream fos = new FileOutputStream(contentZip);
        fos.write(Base64.getDecoder().decode(content.asText()));
        fos.close();

        Decompressor decompressor = new Decompressor.Zip(contentZip);
        decompressor.extract(tmpDir);

        FileUtil.delete(contentZip);

        PsiDirectory psiDirectory = psiFile.getParent();
        if (psiDirectory == null) {
            throw new RuntimeException("Unable to get content directory");
        }

        List<File> files = Optional
                .ofNullable(tmpDir.listFiles())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        for (File file : files) {
            if (file.isDirectory()) {
                psiDirectory.createSubdirectory(file.getName());
                continue;
            }
            String text = Files.readString(file.toPath()).replace("\r\n", "\n");
            String fileName = file.getName();
            PsiFile contentFile = psiDirectory.findFile(fileName);
            if (contentFile == null) {
                contentFile = psiDirectory.createFile(fileName);
            }
            EcosPsiUtils.setContent(contentFile, text);
        }

        FileUtil.delete(tmpDir);

    }

}
