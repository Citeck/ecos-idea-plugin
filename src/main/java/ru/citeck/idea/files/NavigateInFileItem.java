package ru.citeck.idea.files;

import com.intellij.psi.PsiElement;

public record NavigateInFileItem(String name, PsiElement psiElement) {

    @Override
    public String toString() {
        return name;
    }
}
