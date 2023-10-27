package ru.citeck.ecos.completion.json;

import com.intellij.json.JsonLanguage;
import com.intellij.lang.Language;
import ru.citeck.ecos.completion.CompletionProvider;

public interface JsonCompletionProvider extends CompletionProvider {

    @Override
    default Language getLanguage() {
        return JsonLanguage.INSTANCE;
    }

}
