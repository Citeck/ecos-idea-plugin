package ru.citeck.ecos.files.types;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import ru.citeck.ecos.files.AbstractEcosArtifact;
import ru.citeck.ecos.files.SearchScopeName;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SearchScopeName("Form")
public class Form extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "uiserv/form";
    public static final String PATH = "/ui/form/";

    public Form() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
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
