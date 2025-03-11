package ru.citeck.idea.files.types.citeck;

import ecos.com.fasterxml.jackson210.databind.JsonNode;
import com.intellij.psi.PsiFile;
import ru.citeck.idea.files.types.filters.FileExtensionFilter;
import ru.citeck.idea.utils.CiteckPsiUtils;

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
        CiteckPsiUtils.setContent(psiFile, decodedContent);
    }
}
