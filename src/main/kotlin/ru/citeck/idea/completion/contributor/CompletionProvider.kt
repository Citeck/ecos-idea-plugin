package ru.citeck.idea.completion.contributor

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.lang.Language
import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.resolve.FileContextUtil
import com.intellij.util.ProcessingContext

interface CompletionProvider {

    fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    )

    fun getElementPattern(): ElementPattern<out PsiElement>

    fun getCompletionType(): CompletionType = CompletionType.BASIC

    fun getLanguage(): Language

    fun getInjectedInFile(parameters: CompletionParameters): PsiFile? {
        return parameters.originalFile
            .getUserData(FileContextUtil.INJECTED_IN_ELEMENT)
            ?.containingFile
    }
}
