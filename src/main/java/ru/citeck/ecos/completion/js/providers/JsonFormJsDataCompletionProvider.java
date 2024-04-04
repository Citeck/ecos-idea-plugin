package ru.citeck.ecos.completion.js.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.completion.js.JsCompletionProvider;
import ru.citeck.ecos.files.types.ecos.Form;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class JsonFormJsDataCompletionProvider implements JsCompletionProvider {

    @Override
    public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

        PsiFile injectedInFile = getInjectedInFile(parameters);
        if (injectedInFile == null) {
            return;
        }

        if (!ServiceRegistry.getFileTypeService().isInstance(injectedInFile, Form.JSON.class)) {
            return;
        }

        PsiElement psiElement = parameters.getPosition();
        JSReferenceExpression referenceExpression = PsiTreeUtil.getParentOfType(psiElement, JSReferenceExpression.class);
        if (referenceExpression == null) {
            return;
        }

        String qualifier = Optional
                .ofNullable(referenceExpression.getQualifier())
                .map(PsiElement::getText)
                .orElse(null);

        if (qualifier == null) {
            return;
        }

        String[] qualifiers = qualifier.split("\\.");
        if (!"data".equals(qualifiers[0]) || qualifiers.length > 2) {
            return;
        }

        if (qualifiers.length == 1) {
            getInputComponents(injectedInFile, null)
                    .map(component -> getProperties(component, "key", "type"))
                    .forEach(properties -> addElement(result, properties.get("key"), properties.get("type")));
        } else {
            getInputComponents(injectedInFile, qualifiers[1])
                    .flatMap(this::getAsyncDataComponentKeys)
                    .forEach(key -> addElement(result, key, null));
        }

    }

    private void addElement(CompletionResultSet result, String key, String type) {
        if (key == null) {
            return;
        }
        LookupElementBuilder elementBuilder = LookupElementBuilder
                .create(key)
                .withIcon(Icons.CiteckLogo);
        if (type != null) {
            elementBuilder = elementBuilder.withTypeText(type);
        }
        result.addElement(elementBuilder);
    }

    private Stream<String> getAsyncDataComponentKeys(JsonObject component) {

        if (!"asyncData".equals(getProperty(component, "type"))) {
            return Stream.of();
        }

        return Optional
                .of(component)
                .map(c -> c.findProperty("source"))
                .map(JsonProperty::getValue)
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .stream()
                .flatMap(source -> Stream
                        .of(
                                "recordsArray",
                                "record",
                                "recordsQuery"
                        )
                        .map(source::findProperty)
                        .filter(Objects::nonNull)
                )
                .map(JsonProperty::getValue)
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .map(attributesContainer -> attributesContainer.findProperty("attributes"))
                .filter(Objects::nonNull)
                .map(JsonProperty::getValue)
                .filter(JsonObject.class::isInstance)
                .map(JsonObject.class::cast)
                .flatMap(attributes -> attributes.getPropertyList().stream())
                .map(JsonProperty::getName);

    }

    private Stream<JsonObject> getInputComponents(PsiFile psiFile, String key) {
        return Form.JSON
                .getComponents(psiFile)
                .stream()
                .filter(component -> "true".equals(getProperty(component, "input")))
                .filter(component -> key == null || key.equals(getProperty(component, "key")));
    }

    private Map<String, String> getProperties(JsonObject jsonObject, String... property) {
        return EcosPsiUtils.getProperties(jsonObject, property);
    }

    private String getProperty(JsonObject jsonObject, String property) {
        return EcosPsiUtils.getProperty(jsonObject, property);
    }


    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement()
                .withParent(
                        PlatformPatterns.psiElement(JSElementTypes.REFERENCE_EXPRESSION)
                )
                .afterLeaf(".", "?.");
    }
}
