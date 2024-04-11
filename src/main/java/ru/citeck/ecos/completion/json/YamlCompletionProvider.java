package ru.citeck.ecos.completion.json;

import com.intellij.lang.Language;
import org.jetbrains.yaml.YAMLLanguage;
import ru.citeck.ecos.completion.CompletionProvider;

public interface YamlCompletionProvider extends CompletionProvider {
    @Override
    default Language getLanguage() {
        return YAMLLanguage.INSTANCE;
    }
}
