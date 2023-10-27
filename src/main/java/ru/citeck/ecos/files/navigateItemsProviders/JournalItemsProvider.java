package ru.citeck.ecos.files.navigateItemsProviders;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;
import ru.citeck.ecos.files.types.Journal;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Collection;
import java.util.stream.Collectors;

public class JournalItemsProvider implements NavigateInFileItemsProvider {

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!ServiceRegistry.getFileTypeService().isInstance(psiFile, Journal.class)) {
            return null;
        }

        return Journal
            .getColumns(psiFile)
            .stream()
            .map(column -> new NavigateInFileItem(EcosPsiUtils.getProperty(column, "name"), column))
            .collect(Collectors.toList());

    }

}
