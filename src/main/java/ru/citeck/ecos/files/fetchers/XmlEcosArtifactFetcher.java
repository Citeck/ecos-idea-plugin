package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.XmlEcosArtifact;

import java.util.Base64;

public class XmlEcosArtifactFetcher extends AbstractEcosArtifactFetcher {

    @Override
    protected String getContentAttribute() {
        return "data";
    }

    @Override
    protected String postprocessContent(JsonNode content) throws Exception {
        return new String(Base64.getDecoder().decode(content.asText())).replace("\r\n", "\n");
    }

    @Override
    protected Class<? extends EcosArtifact> getFileType() {
        return XmlEcosArtifact.class;
    }

}
