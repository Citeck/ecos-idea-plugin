package ru.citeck.ecos.files.types.ecos.ui;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public interface Dashboard extends EcosArtifact {

    String SOURCE_ID = "uiserv/dashboard";
    String PATH = "/ui/dashboard/";

    class JSON extends JsonEcosArtifact implements Dashboard {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Dashboard {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/interface/dashboards.html";
    }

}
