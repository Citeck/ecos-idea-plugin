package ru.citeck.ecos.languageInjections;

import com.intellij.ide.actions.QualifiedNameProviderUtil;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.json.psi.JsonValue;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.types.ecos.ui.Form;

import java.util.List;
import java.util.Set;

public class EcosFormJavaScriptInjector implements MultiHostInjector {

    private static final Set<String> PATHS = Set.of(
            ".executionCondition",
            ".source.ajax.mapping",
            ".calculateValue",
            ".customDefaultValue",
            ".customConditional",
            ".validate.custom",
            ".javascript",
            ".ajax.data",
            ".dataPreProcessingCode",
            ".data.custom",
            ".displayElementsJS",
            ".customPredicateJs"
    );

    private static final List<? extends Class<? extends PsiElement>> ELEMENTS_TO_INJECT_IN = List.of(JsonProperty.class);

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {

        if (!ServiceRegistry.getFileTypeService().isInstance(context.getContainingFile(), Form.class)) {
            return;
        }

        if (!(context instanceof JsonProperty property)) {
            return;
        }

        JsonValue value = property.getValue();
        if (!(value instanceof JsonStringLiteral literal)) {
            return;
        }

        String qualifiedName = DumbService
                .getInstance(context.getProject())
                .computeWithAlternativeResolveEnabled(() -> QualifiedNameProviderUtil.getQualifiedName(context));

        if (PATHS.stream().noneMatch(qualifiedName::endsWith)) {
            return;
        }

        registrar.startInjecting(JavascriptLanguage.INSTANCE);
        registrar.addPlace(null, null, (PsiLanguageInjectionHost) literal, new TextRange(1, literal.getTextLength() - 1));
        registrar.doneInjecting();

    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return ELEMENTS_TO_INJECT_IN;
    }

}
