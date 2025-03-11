package ru.citeck.idea.languageInjections;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.xmlb.annotations.Attribute;
import lombok.Data;

@Data
public class CiteckFormLanguageInjection {

    public static final ExtensionPointName<CiteckFormLanguageInjection> EP_NAME =
        ExtensionPointName.create("ru.citeck.idea.citeckFormLanguageInjection");

    @Attribute("componentType")
    public String componentType;

    @Attribute("path")
    public String path;
}
