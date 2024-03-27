package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Localization")
public class Localization extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/i18n";

    public Localization() {
        super("/ui/i18n/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
