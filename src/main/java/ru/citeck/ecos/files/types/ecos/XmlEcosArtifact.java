package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.databind.JsonNode;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.Function;

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
    public Function<JsonNode, String> getContentPostprocessor() {
        return jsonNode -> new String(Base64.getDecoder().decode(jsonNode.asText()), StandardCharsets.UTF_8)
                .replace("\r\n", "\n");
    }

}
