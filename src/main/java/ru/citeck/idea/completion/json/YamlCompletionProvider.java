package ru.citeck.idea.completion.json;

import com.intellij.lang.Language;
import org.jetbrains.yaml.YAMLLanguage;
import ru.citeck.idea.completion.CompletionProvider;

public interface YamlCompletionProvider extends CompletionProvider {
    @Override
    default Language getLanguage() {
        return YAMLLanguage.INSTANCE;
    }
}
