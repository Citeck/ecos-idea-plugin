package ru.citeck.ecos.files.types.ecos.model;

import com.intellij.openapi.project.Project;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;
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

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/%D0%A2%D0%B8%D0%BF%D1%8B_%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85.html";
    }

}
