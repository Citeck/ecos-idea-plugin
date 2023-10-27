package ru.citeck.ecos.files.navigateItemsProviders;

import com.intellij.psi.PsiFile;
import com.jgoodies.common.base.Strings;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;
import ru.citeck.ecos.files.types.Form;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class FormItemsProvider implements NavigateInFileItemsProvider {

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!ServiceRegistry.getFileTypeService().isInstance(psiFile, Form.class)) {
            return null;
        }

        return Form
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
