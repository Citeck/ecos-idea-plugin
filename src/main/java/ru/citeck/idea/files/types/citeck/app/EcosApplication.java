package ru.citeck.idea.files.types.citeck.app;

import com.intellij.openapi.diagnostic.Logger;
import ecos.com.fasterxml.jackson210.databind.ObjectMapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.util.io.Compressor;
import lombok.Data;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.commons.json.Json;
import ru.citeck.ecos.commons.json.YamlUtils;
import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;
import ru.citeck.idea.files.types.filters.FileFilter;
import ru.citeck.idea.files.types.filters.FileNameFilter;
import ru.citeck.idea.files.types.filters.FilterAnd;
import ru.citeck.idea.files.types.filters.FilterOr;
import ru.citeck.idea.utils.MavenUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.Deflater;

public interface EcosApplication extends EcosArtifact {

    Logger log = Logger.getInstance(EcosApplication.class);

    String SOURCE_ID = "eapps/ecosapp";
    String PATH = "/src/main/resources/app/";

    @Override
    default boolean canFetch(PsiFile psiFile) {
        return false;
    }

    @Override
    default byte[] getContent(PsiFile psiFile) {

        try {

            Meta meta = getApplicationMeta(psiFile);
            if (meta == null) {
                throw new RuntimeException("Unable to read application meta");
            }

            String applicationVersion = getApplicationVersion(psiFile);
            if (applicationVersion == null) {
                throw new RuntimeException("Unable to read application version");
            }

            meta.version = applicationVersion;
            Path metaPath = psiFile.getVirtualFile().toNioPath();

            File appDirectory = Optional
                .ofNullable(psiFile.getParent())
                .map(PsiDirectory::getVirtualFile)
                .map(VirtualFile::getPath)
                .map(File::new)
                .orElseThrow(() -> new RuntimeException("Unable to determine application path"));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            Compressor.Zip zip = new Compressor
                .Zip(byteArrayOutputStream)
                .withLevel(Deflater.BEST_COMPRESSION);

            zip.filter((fileName, path) -> !metaPath.equals(path));
            zip.addDirectory(appDirectory);
            zip.addFile("meta.json", new ObjectMapper().writeValueAsString(meta).getBytes(StandardCharsets.UTF_8));
            zip.close();

            return byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    default @Nullable Meta getApplicationMeta(PsiFile psiFile) {
        try {
            Object data = YamlUtils.read(psiFile.getText());
            if (data == null) {
                return null;
            }
            return Json.getMapper().convertNotNull(data, Meta.class);
        } catch (Exception e) {
            log.error("Application meta reading failed: " + psiFile.getVirtualFile().getPath(), e);
            return null;
        }
    }

    default @Nullable String getApplicationVersion(PsiFile psiFile) {
        return Optional
            .ofNullable(MavenUtils.getPomFile(psiFile))
            .map(XmlFile::getRootTag)
            .map(xmlTag -> xmlTag.findFirstSubTag("version"))
            .map(XmlTag::getValue)
            .map(XmlTagValue::getText)
            .orElse(null);
    }

    @Data
    class Meta {
        private String id;
        private Object name;
        private String version;
    }

    class JSON extends JsonEcosArtifact implements EcosApplication {
        private final FileFilter filter;

        public JSON() {
            super(PATH, SOURCE_ID);
            this.filter = new FilterAnd(super.getFilter(), new FileNameFilter(PATH + "meta.json"));
        }

        @Override
        public FileFilter getFilter() {
            return filter;
        }

        @Override
        public String getMutationAttribute() {
            return "_content";
        }

    }

    class YAML extends YamlEcosArtifact implements EcosApplication {

        private final FileFilter filter;

        public YAML() {
            super(PATH, SOURCE_ID);
            this.filter = new FilterAnd(super.getFilter(), new FilterOr(
                new FileNameFilter(PATH + "meta.yml"),
                new FileNameFilter(PATH + "meta.yaml")
            ));
        }

        @Override
        public FileFilter getFilter() {
            return filter;
        }

        @Override
        public String getMutationAttribute() {
            return "_content";
        }

    }

}
