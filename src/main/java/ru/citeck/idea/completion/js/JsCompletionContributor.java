package ru.citeck.idea.completion.js;

import com.intellij.lang.Language;
import com.intellij.lang.javascript.JavascriptLanguage;
import ru.citeck.idea.completion.CompletionContributor;

public class JsCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return JavascriptLanguage.INSTANCE;
    }
}
