package ru.citeck.idea.codeinsight.forms.components;

import ecos.com.fasterxml.jackson210.annotation.JsonProperty;

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
