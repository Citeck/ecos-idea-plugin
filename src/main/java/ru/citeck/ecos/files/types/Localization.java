package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Localization")
public class Localization extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/i18n";
    public static final String PATH = "/ui/i18n/";

    public Localization() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
