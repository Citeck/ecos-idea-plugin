package ru.citeck.idea.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.lang.Language;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.psi.impl.source.resolve.FileContextUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CompletionProvider {

    void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result);

    ElementPattern<? extends PsiElement> getElementPattern();

    default CompletionType getCompletionType() {
        return CompletionType.BASIC;
    }

    Language getLanguage();

    default PsiFile getInjectedInFile(CompletionParameters parameters) {
        return Optional
            .of(parameters.getOriginalFile())
            .map(psiFile -> psiFile.getUserData(FileContextUtil.INJECTED_IN_ELEMENT))
            .map(SmartPsiElementPointer::getContainingFile)
            .orElse(null);
    }

}
