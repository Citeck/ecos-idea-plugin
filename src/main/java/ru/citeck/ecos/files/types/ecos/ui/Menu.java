package ru.citeck.ecos.files.types.ecos.ui;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public interface Menu extends EcosArtifact {

    String SOURCE_ID = "uiserv/menu";
    String PATH = "/ui/menu/";

    class JSON extends JsonEcosArtifact implements Menu {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Menu {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/interface/menu.html";
    }

}
