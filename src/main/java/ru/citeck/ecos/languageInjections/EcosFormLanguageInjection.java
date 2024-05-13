package ru.citeck.ecos.languageInjections;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.xmlb.annotations.Attribute;
import lombok.Data;

@Data
public class EcosFormLanguageInjection {

    public static final ExtensionPointName<EcosFormLanguageInjection> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.ecosFormLanguageInjection");

    @Attribute("componentType")
    public String componentType;

    @Attribute("path")
    public String path;

}
