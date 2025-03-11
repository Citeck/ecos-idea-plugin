package ru.citeck.idea.completion.json;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.json.psi.JsonProperty;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class JsonTypeRefCompletionProvider implements JsonCompletionProvider, TypeRefCompletionProvider {
    @Override
    public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        createTypeRefsCompletions(result, parameters.getPosition().getProject());
    }

    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement().inside(
            PlatformPatterns.psiElement(JsonProperty.class)
                .withName(
                    PlatformPatterns.string().oneOf("typeRef")
                ));
    }

}
