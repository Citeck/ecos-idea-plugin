package ru.citeck.idea.completion.js

import com.intellij.lang.Language
import com.intellij.lang.javascript.JavascriptLanguage
import ru.citeck.idea.completion.js.contributor.CompletionContributor

class JsCompletionContributor : CompletionContributor() {
    override fun getLanguage(): Language {
        return JavascriptLanguage
    }
}
