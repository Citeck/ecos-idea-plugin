package ru.citeck.ecos.references;

import com.intellij.psi.PsiElement;

public interface PsiElementValueResolver<T extends PsiElement> {

    Class<T> getPsiElementType();

    Object getValue(T psiElement);

    @SuppressWarnings("unchecked")
    default Object getPsiElementValue(PsiElement psiElement) {
        if (getPsiElementType().isInstance(psiElement)) {
            return getValue((T) psiElement);
        }
        return null;
    }

}
