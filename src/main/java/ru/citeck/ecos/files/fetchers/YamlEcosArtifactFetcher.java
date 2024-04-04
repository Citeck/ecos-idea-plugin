package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public class YamlEcosArtifactFetcher extends AbstractEcosArtifactFetcher{
    @Override
    protected String getContentAttribute() {
        return ".json|yaml()";
    }

    @Override
    protected String postprocessContent(JsonNode content) {
        return content.asText().replace("\r\n", "\n");
    }

    @Override
    protected Class<? extends EcosArtifact> getFileType() {
        return YamlEcosArtifact.class;
    }

}
