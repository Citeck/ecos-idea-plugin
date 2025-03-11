package ru.citeck.idea.completion.js;

import com.intellij.lang.Language;
import com.intellij.lang.javascript.JavascriptLanguage;
import ru.citeck.idea.completion.CompletionProvider;

public interface JsCompletionProvider extends CompletionProvider {

    @Override
    default Language getLanguage() {
        return JavascriptLanguage.INSTANCE;
    }

}
