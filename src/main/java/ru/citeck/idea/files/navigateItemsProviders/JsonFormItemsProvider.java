package ru.citeck.idea.files.navigateItemsProviders;

import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import ru.citeck.idea.files.NavigateInFileItem;
import ru.citeck.idea.files.NavigateInFileItemsProvider;
import ru.citeck.idea.files.types.citeck.ui.Form;
import ru.citeck.idea.utils.CiteckPsiUtils;

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
                String type = CiteckPsiUtils.getProperty(component, "type");
                String key = CiteckPsiUtils.getProperty(component, "key");

                if (Strings.isEmpty(type) || "column".equals(type)) {
                    return null;
                }
                return new NavigateInFileItem(type + "@" + key, component);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    }

}
