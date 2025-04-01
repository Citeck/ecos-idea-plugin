package ru.citeck.idea.completion.js

import com.intellij.lang.Language
import com.intellij.lang.javascript.JavascriptLanguage
import ru.citeck.idea.completion.contributor.CompletionProvider

interface JsCompletionProvider : CompletionProvider {
    override fun getLanguage(): Language {
        return JavascriptLanguage.INSTANCE
    }
}
