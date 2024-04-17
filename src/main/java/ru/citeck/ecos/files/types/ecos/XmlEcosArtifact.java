package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class XmlEcosArtifact extends AbstractEcosArtifact {
    public XmlEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.XML, sourceId);
    }

    @Override
    public String getMimeType() {
        return "text/xml";
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
    public void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception {
        String decodedContent = new String(Base64.getDecoder().decode(content.asText()), StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
        EcosPsiUtils.setContent(psiFile, decodedContent);
    }

}
