package ru.citeck.idea.completion.json;

import com.intellij.lang.Language;
import org.jetbrains.yaml.YAMLLanguage;
import ru.citeck.idea.completion.CompletionContributor;

public class YamlCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return YAMLLanguage.INSTANCE;
    }
}
