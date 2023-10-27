package ru.citeck.ecos.completion.js;

import com.intellij.lang.Language;
import com.intellij.lang.javascript.JavascriptLanguage;
import ru.citeck.ecos.completion.CompletionContributor;

public class JsCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return JavascriptLanguage.INSTANCE;
    }
}
