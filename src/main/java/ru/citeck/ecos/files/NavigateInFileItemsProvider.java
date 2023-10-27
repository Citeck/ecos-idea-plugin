package ru.citeck.ecos.files;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface NavigateInFileItemsProvider {

    @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile);


}
