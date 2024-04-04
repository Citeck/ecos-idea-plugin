package ru.citeck.ecos.files.types.ecos;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;

public abstract class JsonEcosArtifact extends AbstractEcosArtifact {
    public JsonEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.JSON, sourceId);
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
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

}
