package ru.citeck.idea.files.types.citeck;

import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ru.citeck.idea.files.FileType;

import java.util.Collections;
import java.util.List;

public interface EcosArtifact extends FileType {

    String getSourceId();

    PsiElement getIdPsiElement(PsiFile psiFile);

    default List<String> getAdditionalReferences(String artifactId) {
        return Collections.emptyList();
    }

    default String getId(PsiFile psiFile) {
        PsiElement idProperty = getIdPsiElement(psiFile);
        if (idProperty == null) {
            return null;
        }
        return JsonPsiUtil.stripQuotes(idProperty.getText());
    }

    default byte[] getContent(PsiFile psiFile) {
        return psiFile
            .getText()
            .getBytes(psiFile.getVirtualFile().getCharset());
    }

    default String getFullId(PsiFile psiFile) {
        return getSourceId() + "@" + getId(psiFile);
    }

    default boolean isIndexable(PsiFile psiFile) {
        return true;
    }
}
