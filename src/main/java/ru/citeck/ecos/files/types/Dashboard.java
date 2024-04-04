package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Dashboard")
public class Dashboard extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/dashboard";
    public static final String PATH = "/ui/dashboard/";

    public Dashboard() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
