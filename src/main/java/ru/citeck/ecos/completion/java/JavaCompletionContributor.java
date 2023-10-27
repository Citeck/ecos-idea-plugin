package ru.citeck.ecos.completion.java;

import com.intellij.lang.Language;
import com.intellij.lang.java.JavaLanguage;
import ru.citeck.ecos.completion.CompletionContributor;

public class JavaCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }
}
