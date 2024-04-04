package ru.citeck.ecos.files.navigateItemsProviders;

import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;
import ru.citeck.ecos.files.types.ecos.Form;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonFormItemsProvider implements NavigateInFileItemsProvider {

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!(psiFile instanceof JsonFile)) {
            return null;
        }

        return Form.JSON
                .getComponents(psiFile)
                .stream()
                .map(component -> {
                    String type = EcosPsiUtils.getProperty(component, "type");
                    String key = EcosPsiUtils.getProperty(component, "key");

                    if (Strings.isEmpty(type) || "column".equals(type)) {
                        return null;
                    }
                    return new NavigateInFileItem(type + "@" + key, component);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

}
