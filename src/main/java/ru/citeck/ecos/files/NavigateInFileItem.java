package ru.citeck.ecos.files;

import com.intellij.psi.PsiElement;
import lombok.Data;

@Data
public class NavigateInFileItem {

    private final String name;
    private final PsiElement psiElement;

    @Override
    public String toString() {
        return name;
    }

}
