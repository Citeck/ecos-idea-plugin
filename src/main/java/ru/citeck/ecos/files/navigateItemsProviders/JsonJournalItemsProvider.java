package ru.citeck.ecos.files.navigateItemsProviders;

import com.intellij.json.psi.JsonFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;
import ru.citeck.ecos.files.types.ecos.Journal;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonJournalItemsProvider implements NavigateInFileItemsProvider {

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!(psiFile instanceof JsonFile)) {
            return null;
        }

        return Optional
                .ofNullable(ServiceRegistry.getFileTypeService().getFileType(psiFile))
                .filter(Journal.JSON.class::isInstance)
                .map(Journal.JSON.class::cast)
                .map(journal -> Journal.JSON.getColumns(psiFile))
                .map(columns -> columns
                        .stream()
                        .map(column -> new NavigateInFileItem(EcosPsiUtils.getProperty(column, "id"), column))
                        .collect(Collectors.toList())
                )
                .orElse(null);
    }

}
