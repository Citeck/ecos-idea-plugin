package ru.citeck.ecos.references;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiElement;

public interface PsiElementValueResolver<T extends PsiElement> {

    ExtensionPointName<PsiElementValueResolver<?>> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.psiElementValueResolver");

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
