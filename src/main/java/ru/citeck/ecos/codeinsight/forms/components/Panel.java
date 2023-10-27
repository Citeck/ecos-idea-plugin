package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Panel extends Container {

    public static final String TYPE = "panel";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    @JsonProperty("title")
    public String getLabel() {
        return super.getLabel();
    }
}
