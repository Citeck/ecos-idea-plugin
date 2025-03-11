package ru.citeck.idea.files.types.citeck.model;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface PermissionSettings extends EcosArtifact {

    String SOURCE_ID = "emodel/permission-settings";
    String PATH = "/model/permission-settings";

    class JSON extends JsonEcosArtifact implements PermissionSettings {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements PermissionSettings {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }


}
