package ru.citeck.ecos.codeinsight.forms.components;

public class Column extends Container {

    public static final String TYPE = "column";

    private Integer index = 0;

    @Override
    public String getType() {
        return TYPE;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String getKey() {
        return "column";
    }

}
