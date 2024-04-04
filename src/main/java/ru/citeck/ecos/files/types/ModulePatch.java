package ru.citeck.ecos.files.types;

import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

@SearchScopeName("Module patch")
public class ModulePatch extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "eapps/artifact-patch";

    public ModulePatch() {
        super("/app/module-patch/");
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

}
