package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    private String label = "";
    private String key = "";
    private List<String> refreshOn = new ArrayList<>();

    @JsonProperty("type")
    public abstract String getType();

    @JsonProperty("input")
    boolean getInput() {
        return false;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getRefreshOn() {
        return refreshOn;
    }

    public void setRefreshOn(List<String> refreshOn) {
        this.refreshOn = refreshOn;
    }

}
