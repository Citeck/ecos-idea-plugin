package ru.citeck.idea.files.scopes;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.xmlb.annotations.Attribute;

public class EcosSearchScope {

    public static final ExtensionPointName<EcosSearchScope> EP_NAME =
        ExtensionPointName.create("ru.citeck.idea.searchScope");

    @Attribute("name")
    public String name;

    @Attribute("class")
    public String clazz;

}
