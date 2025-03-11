package ru.citeck.idea.files.navigateItemsProviders;

import com.intellij.json.psi.JsonFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.NavigateInFileItem;
import ru.citeck.idea.files.NavigateInFileItemsProvider;
import ru.citeck.idea.files.types.citeck.ui.Journal;
import ru.citeck.idea.utils.CiteckPsiUtils;

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
            .ofNullable(FileTypeService.getInstance().getFileType(psiFile))
            .filter(Journal.JSON.class::isInstance)
            .map(Journal.JSON.class::cast)
            .map(journal -> Journal.JSON.getColumns(psiFile))
            .map(columns -> columns
                .stream()
                .map(column -> new NavigateInFileItem(CiteckPsiUtils.getProperty(column, "id"), column))
                .collect(Collectors.toList())
            )
            .orElse(null);
    }

}
