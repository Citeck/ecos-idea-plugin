package ru.citeck.idea.files.types.citeck.model;

import com.intellij.openapi.project.Project;

import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;
import ru.citeck.idea.index.IndexKey;
import ru.citeck.idea.index.IndexValue;
import ru.citeck.idea.index.IndexesService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface DataType extends EcosArtifact {

    String SOURCE_ID = "emodel/types-repo";
    String PATH = "/model/type/";
    String EMODEL_TYPE = "emodel/type@";

    static List<String> getDataTypes(Project project) {
        return IndexesService.getInstance(project)
            .stream(new IndexKey(SOURCE_ID))
            .map(IndexValue::getId)
            .sorted()
            .collect(Collectors.toList());
    }

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

}
