package ru.citeck.ecos.files.types;

import com.intellij.json.psi.*;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.EcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.files.types.filters.FileFilter;
import ru.citeck.ecos.files.types.filters.FilterAnd;
import ru.citeck.ecos.files.types.filters.FolderNamePatternsFilter;

import java.util.List;
import java.util.stream.Collectors;

@SearchScopeName("Journal")
public class Journal implements BrowsableEcosArtifact {

    public static final String SOURCE_ID = "uiserv/journal";

    private final FilterAnd filter = new FilterAnd(
        FileExtensionFilter.JSON,
        new FolderNamePatternsFilter("/ui/journal/")
    );

    @Override
    public FileFilter getFilter() {
        return filter;
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

    public static List<JsonObject> getColumns(PsiFile psiFile) {

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
