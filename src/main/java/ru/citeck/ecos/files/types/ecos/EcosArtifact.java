package ru.citeck.ecos.files.types.ecos;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.FileType;

import java.util.Collections;
import java.util.List;

public interface EcosArtifact extends FileType {

    String getSourceId();

    default String getId(PsiFile psiFile) {
        PsiElement idProperty = getIdPsiElement(psiFile);
        if (idProperty == null) {
            return null;
        }
        return JsonPsiUtil.stripQuotes(idProperty.getText());
    }

    PsiElement getIdPsiElement(PsiFile psiFile);

    default List<String> getAdditionalReferences(String artifactId) {
        return Collections.emptyList();
    }

}
