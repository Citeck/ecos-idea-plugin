package ru.citeck.ecos.completion.json;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import ru.citeck.ecos.completion.CompletionContributor;

public class JsonCompletionContributor extends CompletionContributor {
    @Override
    public Language getLanguage() {
        return JsonLanguage.INSTANCE;
    }
}
