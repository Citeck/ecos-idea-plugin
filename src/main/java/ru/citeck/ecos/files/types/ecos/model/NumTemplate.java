package ru.citeck.ecos.files.types.ecos.model;

import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

public interface NumTemplate extends EcosArtifact {

    String SOURCE_ID = "emodel/num-template";
    String PATH = "/model/num-template/";

    class JSON extends JsonEcosArtifact implements NumTemplate {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements NumTemplate {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/number_template.html";
    }

}
