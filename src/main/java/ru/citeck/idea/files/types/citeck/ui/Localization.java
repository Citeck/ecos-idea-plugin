package ru.citeck.idea.files.types.citeck.ui;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface Localization extends EcosArtifact {

    String SOURCE_ID = "uiserv/i18n";
    String PATH = "/ui/i18n/";

    class JSON extends JsonEcosArtifact implements Localization {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements Localization {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
