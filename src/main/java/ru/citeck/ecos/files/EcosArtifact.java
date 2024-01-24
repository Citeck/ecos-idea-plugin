package ru.citeck.ecos.files;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;

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

    default PsiElement getIdPsiElement(PsiFile psiFile) {
        if (!(psiFile instanceof PsiFileImpl)) {
            return null;
        }
        JsonObject jsonObject = ((PsiFileImpl) psiFile).findChildByClass(JsonObject.class);
        if (jsonObject == null) {
            return null;
        }
        JsonProperty idProperty = jsonObject.findProperty("id");
        if (idProperty == null) {
            return null;
        }
        return idProperty.getValue();
    }

    default List<String> getAdditionalReferences(String artifactId) {
        return Collections.emptyList();
    }

}
