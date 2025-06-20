package ru.citeck.ecos.files.types.ecos.model;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonSchemaArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public interface Permissions extends EcosArtifact, JsonSchemaArtifact {

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

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/%D0%A2%D0%B8%D0%BF%D1%8B_%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85.html#permissions";
    }

    @Override
    default String getSchemaName() {
        return "permissions-schema.json";
    }

}
