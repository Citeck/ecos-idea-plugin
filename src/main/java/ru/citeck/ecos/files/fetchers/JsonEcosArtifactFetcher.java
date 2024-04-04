package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.utils.JsonPrettyPrinter;

public class JsonEcosArtifactFetcher extends AbstractEcosArtifactFetcher {

    private final ObjectWriter objectWriter = new ObjectMapper().writer(new JsonPrettyPrinter());

    @Override
    protected String getContentAttribute() {
        return ".json";
    }

    @Override
    protected String postprocessContent(JsonNode content) throws JsonProcessingException {
        return objectWriter.writeValueAsString(content).replace("\r\n", "\n");
    }

    @Override
    protected Class<? extends EcosArtifact> getFileType() {
        return JsonEcosArtifact.class;
    }

}
