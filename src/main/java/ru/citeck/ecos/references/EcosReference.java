package ru.citeck.ecos.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.index.IndexValue;

public class EcosReference extends PsiReferenceBase<PsiElement> {

    private final IndexValue indexValue;

    public EcosReference(@NotNull PsiElement element, IndexValue indexValue) {
        super(element);
        this.indexValue = indexValue;
    }

    @Override
    public @Nullable PsiElement resolve() {
        return indexValue.getPsiElement(getElement().getProject());
    }
}
