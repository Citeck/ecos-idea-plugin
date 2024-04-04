package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.FileType;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface EcosArtifact extends FileType {

    String getSourceId();

    PsiElement getIdPsiElement(PsiFile psiFile);

    String getMimeType();

    String getMutationAttribute();

    String getContentAttribute();

    Function<JsonNode, String> getContentPostprocessor();

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

}
