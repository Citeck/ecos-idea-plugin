package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Panel extends Container {

    public static final String TYPE = "panel";

    @Override
    public String getType() {
        return TYPE;
    }

    @JsonProperty("title")
    public Map<String, String> getTitle() {
        return Map.of(
                "ru", "",
                "en", ""
        );
    }

}
