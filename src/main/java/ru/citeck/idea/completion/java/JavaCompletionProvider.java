package ru.citeck.idea.completion.java;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import ru.citeck.idea.completion.CompletionProvider;

public interface JavaCompletionProvider extends CompletionProvider {

    @Override
    default Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }

}
