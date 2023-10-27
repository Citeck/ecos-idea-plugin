package ru.citeck.ecos.codeinsight.forms.components;

import java.util.Map;

public class EcosSelect extends PropertyComponent {

    public static final String TYPE = "ecosSelect";

    private final Map<String, String> data = Map.of("url", "/citeck/ecos/records/query");
    private final Boolean lazyLoad = false;

    @Override
    public String getType() {
        return TYPE;
    }

    public Map<String, String> getData() {
        return data;
    }

    public Boolean getLazyLoad() {
        return lazyLoad;
    }

}
