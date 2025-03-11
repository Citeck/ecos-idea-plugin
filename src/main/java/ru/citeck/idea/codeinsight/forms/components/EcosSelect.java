package ru.citeck.idea.codeinsight.forms.components;

import lombok.Getter;

import java.util.Map;

@Getter
public class EcosSelect extends PropertyComponent {

    public static final String TYPE = "ecosSelect";

    private final Map<String, String> data = Map.of("url", "/citeck/ecos/records/query");
    private final Boolean lazyLoad = false;

    @Override
    public String getType() {
        return TYPE;
    }

}
