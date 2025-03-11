package ru.citeck.idea.files.types.citeck.ui;

import com.intellij.json.psi.*;
import com.intellij.psi.PsiFile;
import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface Journal extends EcosArtifact {

    String SOURCE_ID = "uiserv/journal";
    String PATH = "/ui/journal/";

    class JSON extends JsonEcosArtifact implements Journal {
        public JSON() {
            super(PATH, SOURCE_ID);
        }

        public static List<JsonObject> getColumns(PsiFile psiFile) {

            if (!(psiFile instanceof JsonFile jsonFile)) {
                return Collections.emptyList();
            }

            JsonValue root = jsonFile.getTopLevelValue();
            if (!(root instanceof JsonObject)) {
                return List.of();
            }

            JsonProperty columns = ((JsonObject) root).findProperty("columns");
            if (columns == null || !(columns.getValue() instanceof JsonArray)) {
                return List.of();
            }

            return ((JsonArray) columns.getValue())
                .getValueList()
                .stream()
                .filter(column -> column instanceof JsonObject)
                .map(column -> (JsonObject) column)
                .collect(Collectors.toList());
        }

    }

    class YAML extends YamlEcosArtifact implements Journal {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
