package ru.citeck.ecos.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class CompletionContributor extends com.intellij.codeInsight.completion.CompletionContributor {

    public static final ExtensionPointName<CompletionProvider> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.completionProvider");

    public CompletionContributor() {
        EP_NAME
                .getExtensionsIfPointIsRegistered()
                .stream()
                .filter(completionProvider -> Objects.equals(completionProvider.getLanguage(), getLanguage()))
                .forEach(completionProvider -> extend(
                        completionProvider.getCompletionType(),
                        completionProvider.getElementPattern(),
                        new com.intellij.codeInsight.completion.CompletionProvider<>() {
                            @Override
                            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                                completionProvider.addCompletions(parameters, context, result);
                            }
                        }));
    }

    public abstract Language getLanguage();

}
