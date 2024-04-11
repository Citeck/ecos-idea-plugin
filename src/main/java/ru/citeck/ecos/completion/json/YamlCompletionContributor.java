package ru.citeck.ecos.completion.json;

import com.intellij.lang.Language;
import org.jetbrains.yaml.YAMLLanguage;
import ru.citeck.ecos.completion.CompletionContributor;

public class YamlCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return YAMLLanguage.INSTANCE;
    }
}
