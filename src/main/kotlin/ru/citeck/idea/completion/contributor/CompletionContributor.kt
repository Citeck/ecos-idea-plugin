package ru.citeck.idea.completion.contributor

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.lang.Language
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.ProcessingContext

abstract class CompletionContributor : CompletionContributor() {

/*    companion object {
        val EP_NAME: ExtensionPointName<CompletionProvider> =
            ExtensionPointName.create("ru.citeck.idea.completionProvider")
    }

    init {
        EP_NAME.extensionsIfPointIsRegistered
            .stream()
            .filter { completionProvider: CompletionProvider -> completionProvider.getLanguage() == getLanguage() }
            .forEach { completionProvider: CompletionProvider ->
                extend(
                    completionProvider.getCompletionType(),
                    completionProvider.getElementPattern(),
                    object : com.intellij.codeInsight.completion.CompletionProvider<CompletionParameters>() {
                        override fun addCompletions(
                            parameters: CompletionParameters,
                            context: ProcessingContext,
                            result: CompletionResultSet
                        ) {
                            completionProvider.addCompletions(parameters, context, result)
                        }
                    })
            }
    }*/

    abstract fun getLanguage(): Language
}
