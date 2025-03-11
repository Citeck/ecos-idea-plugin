package ru.citeck.idea.languageInjections;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.types.citeck.ui.Form;
import ru.citeck.idea.utils.CiteckPsiUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EcosFormLanguageInjector implements MultiHostInjector {

    private static final List<? extends Class<? extends PsiElement>> ELEMENTS_TO_INJECT_IN = List.of(JsonObject.class);

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {

        if (!FileTypeService.getInstance().isInstance(context.getContainingFile(), Form.class)) {
            return;
        }

        if (!(context instanceof JsonObject jsonObject)) {
            return;
        }

        String componentType = CiteckPsiUtils.getProperty(jsonObject, "type");
        if (componentType.isEmpty()) {
            return;
        }

        CiteckFormLanguageInjection
            .EP_NAME
            .getExtensionsIfPointIsRegistered()
            .stream()
            .filter(p -> "*".equals(p.componentType) || p.componentType.equals(componentType))
            .map(CiteckFormLanguageInjection::getPath)
            .map(path -> getJsonValueLiteralByPath(jsonObject, path))
            .filter(Objects::nonNull)
            .forEach(literal -> inject(registrar, literal));

    }

    private void inject(@NotNull MultiHostRegistrar registrar, @NotNull JsonStringLiteral literal) {
        registrar.startInjecting(JavascriptLanguage.INSTANCE);
        registrar.addPlace(null, null, (PsiLanguageInjectionHost) literal, new TextRange(1, literal.getTextLength() - 1));
        registrar.doneInjecting();
    }

    private JsonStringLiteral getJsonValueLiteralByPath(JsonObject jsonObject, String path) {
        if (!path.contains("/")) {
            return Optional
                .ofNullable(jsonObject.findProperty(path))
                .map(JsonProperty::getValue)
                .filter(JsonStringLiteral.class::isInstance)
                .map(JsonStringLiteral.class::cast)
                .orElse(null);
        }
        int index = path.indexOf("/");
        String property = path.substring(0, index);
        return Optional
            .ofNullable(jsonObject.findProperty(property))
            .map(JsonProperty::getValue)
            .filter(JsonObject.class::isInstance)
            .map(JsonObject.class::cast)
            .map(nested -> getJsonValueLiteralByPath(nested, path.substring(index + 1)))
            .orElse(null);
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return ELEMENTS_TO_INJECT_IN;
    }

}
