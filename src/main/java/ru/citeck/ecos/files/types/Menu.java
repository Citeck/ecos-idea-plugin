package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Menu")
public class Menu extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/menu";

    public Menu() {
        super("/ui/menu/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
