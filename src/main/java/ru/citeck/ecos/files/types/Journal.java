package ru.citeck.ecos.files.types;

import com.intellij.json.psi.*;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SearchScopeName("Journal")
public class Journal extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/journal";
    public static final String PATH = "/ui/journal/";

    public Journal() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

    public static List<JsonObject> getColumns(PsiFile psiFile) {

        if (!(psiFile instanceof JsonFile)) {
            return Collections.emptyList();
        }

        JsonFile jsonFile = (JsonFile) psiFile;
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
