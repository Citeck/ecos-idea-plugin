package ru.citeck.ecos.files.types.ecos;

import java.util.Collections;
import java.util.List;

public interface DataType extends EcosArtifact {

    String SOURCE_ID = "emodel/types-repo";
    String PATH = "/model/type/";

    @Override
    default List<String> getAdditionalReferences(String artifactId) {
        return Collections.singletonList("emodel/type@" + artifactId);
    }

    class JSON extends JsonEcosArtifact implements DataType {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements DataType {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
