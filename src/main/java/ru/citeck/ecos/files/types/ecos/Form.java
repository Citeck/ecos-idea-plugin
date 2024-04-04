package ru.citeck.ecos.files.types.ecos;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

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

}
