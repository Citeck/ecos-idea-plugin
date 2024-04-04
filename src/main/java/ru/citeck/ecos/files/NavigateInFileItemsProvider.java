package ru.citeck.ecos.files;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface NavigateInFileItemsProvider {

    ExtensionPointName<NavigateInFileItemsProvider> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.navigateInFileItemsProvider");

    @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile);


}
