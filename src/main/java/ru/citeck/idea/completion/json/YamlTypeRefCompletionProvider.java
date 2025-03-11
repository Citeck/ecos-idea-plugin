package ru.citeck.idea.completion.json;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

public class YamlTypeRefCompletionProvider implements YamlCompletionProvider, TypeRefCompletionProvider {

    @Override
    public void addCompletions(
        @NotNull CompletionParameters parameters,
        @NotNull ProcessingContext context,
        @NotNull CompletionResultSet result
    ) {
        createTypeRefsCompletions(result, parameters.getPosition().getProject());
    }

    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement().inside(
            PlatformPatterns.psiElement(YAMLKeyValue.class)
                .withName(PlatformPatterns.string().oneOf("typeRef"))
        );
    }

}
