package ru.citeck.ecos.files.types.ecos.ui;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.files.types.ecos.JsonEcosArtifact;
import ru.citeck.ecos.files.types.ecos.YamlEcosArtifact;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Form extends EcosArtifact {

    String SOURCE_ID = "uiserv/form";
    String PATH = "/ui/form/";

    class JSON extends JsonEcosArtifact implements Form {
        public JSON() {
            super(PATH, SOURCE_ID);
        }

        public static List<JsonObject> getComponents(PsiFile psiFile) {
            return PsiTreeUtil
                    .findChildrenOfType(psiFile, JsonProperty.class)
                    .stream()
                    .filter(jsonProperty -> "components".equals(jsonProperty.getName()))
                    .filter(jsonProperty -> jsonProperty.getValue() instanceof JsonArray)
                    .map(jsonProperty -> (JsonArray) jsonProperty.getValue())
                    .flatMap(components -> Arrays.stream(components.getChildren()))
                    .filter(component -> component instanceof JsonObject)
                    .map(component -> (JsonObject) component)
                    .collect(Collectors.toList());
        }

    }

    class YAML extends YamlEcosArtifact implements Form {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/interface/forms.html";
    }
}
