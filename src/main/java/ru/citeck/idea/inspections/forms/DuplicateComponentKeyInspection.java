package ru.citeck.idea.inspections.forms;

import com.intellij.codeInspection.*;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.types.citeck.ui.Form;
import ru.citeck.idea.utils.CiteckPsiUtils;
import ru.citeck.idea.utils.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuplicateComponentKeyInspection extends LocalInspectionTool {

    private static final Set<String> COMPONENT_TYPES_WITH_NEW_SCOPE = Set.of(
        "datagrid", "container"
    );

    @Override
    public @Nullable ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {

        if (!FileTypeService.getInstance().isInstance(file, Form.JSON.class)) {
            return null;
        }

        JsonObject rootComponent = Optional
            .ofNullable(((JsonFile) file).getTopLevelValue())
            .map(CommonUtils.filterAndCast(JsonObject.class))
            .map(jsonObject -> jsonObject.findProperty("definition"))
            .map(JsonProperty::getValue)
            .map(CommonUtils.filterAndCast(JsonObject.class))
            .orElse(null);


        if (rootComponent == null) {
            return null;
        }

        Map<String, List<JsonObject>> componentsByScope = new HashMap<>();
        getComponentsByScope(rootComponent, "", componentsByScope);

        return componentsByScope
            .values()
            .stream()
            .flatMap(components -> getProblemDescriptor(components, manager))
            .toArray(ProblemDescriptor[]::new);

    }

    private Stream<ProblemDescriptor> getProblemDescriptor(List<JsonObject> components, @NotNull InspectionManager manager) {
        return components
            .stream()
            .filter(component -> {
                String type = CiteckPsiUtils.getProperty(component, "type");
                return Strings.isNotEmpty(type) && !"column".equals(type);
            })
            .collect(Collectors.groupingBy(component -> CiteckPsiUtils.getProperty(component, "key")))
            .entrySet()
            .stream()
            .filter(e -> e.getValue().size() > 1)
            .flatMap(e -> e.getValue().stream())
            .map(component -> component.findProperty("key"))
            .filter(Objects::nonNull)
            .map(componentKey -> manager.createProblemDescriptor(
                componentKey,
                "Duplicate component key",
                (LocalQuickFix) null,
                ProblemHighlightType.ERROR,
                false)
            );
    }

    private void getComponentsByScope(JsonObject component, String scope, Map<String, List<JsonObject>> componentsByScope) {

        if (!componentsByScope.containsKey(scope)) {
            componentsByScope.put(scope, new ArrayList<>());
        }
        List<JsonObject> components = componentsByScope.get(scope);

        String type = CiteckPsiUtils.getProperty(component, "type");
        String key = CiteckPsiUtils.getProperty(component, "key");

        if (Strings.isNotEmpty(type) && Strings.isNotEmpty(key) && !"column".equals(type)) {
            components.add(component);
        }

        Optional
            .ofNullable(component.findProperty("columns".equals(type) ? "columns" : "components"))
            .map(JsonProperty::getValue)
            .map(CommonUtils.filterAndCast(JsonArray.class))
            .map(JsonArray::getValueList)
            .stream()
            .flatMap(Collection::stream)
            .map(CommonUtils.filterAndCast(JsonObject.class))
            .filter(Objects::nonNull)
            .forEach(jsonObject -> getComponentsByScope(
                jsonObject,
                COMPONENT_TYPES_WITH_NEW_SCOPE.contains(type) ? scope + "/" + key : scope,
                componentsByScope
            ));

    }

}
