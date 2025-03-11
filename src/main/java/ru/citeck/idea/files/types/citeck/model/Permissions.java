package ru.citeck.idea.files.types.citeck.model;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface Permissions extends EcosArtifact {

    String SOURCE_ID = "emodel/perms";
    String PATH = "/model/permissions";

    class JSON extends JsonEcosArtifact implements Permissions {
        public JSON() {
            super(PATH, SOURCE_ID);
        }

        @Override
        public String getContentAttribute() {
            return "_self{id:.id|rxg('emodel/perms@(.+)'), typeRef?id, permissions?json, attributes?json}";
        }
    }

    class YAML extends YamlEcosArtifact implements Permissions {
        public YAML() {
            super(PATH, SOURCE_ID);
        }

        @Override
        public String getContentAttribute() {
            return "_self{id:.id|rxg('emodel/perms@(.+)'), typeRef?id, permissions?json, attributes?json}|yaml()";
        }
    }

}
