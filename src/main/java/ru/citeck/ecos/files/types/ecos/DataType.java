package ru.citeck.ecos.files.types.ecos;

import com.intellij.openapi.project.Project;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface DataType extends EcosArtifact {

    String SOURCE_ID = "emodel/types-repo";
    String PATH = "/model/type/";
    String EMODEL_TYPE = "emodel/type@";

    @Override
    default List<String> getAdditionalReferences(String artifactId) {
        return Collections.singletonList(EMODEL_TYPE + artifactId);
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

    static List<String> getDataTypes(Project project) {
        return ServiceRegistry
                .getIndexesService(project)
                .stream(new IndexKey(SOURCE_ID))
                .map(IndexValue::getId)
                .sorted()
                .collect(Collectors.toList());
    }

}
