package ru.citeck.idea.completion.java;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import ru.citeck.idea.completion.CompletionContributor;

public class JavaCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }
}
