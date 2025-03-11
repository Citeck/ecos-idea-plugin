package ru.citeck.idea.files.types.citeck;

import ecos.com.fasterxml.jackson210.databind.JsonNode;
import ecos.com.fasterxml.jackson210.databind.ObjectMapper;
import ecos.com.fasterxml.jackson210.databind.ObjectWriter;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import ru.citeck.idea.files.types.filters.FileExtensionFilter;
import ru.citeck.idea.utils.CiteckPsiUtils;
import ru.citeck.idea.utils.JsonPrettyPrinter;

public abstract class JsonEcosArtifact extends AbstractEcosArtifact {

    public JsonEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.JSON, sourceId);
    }

    @Override
    public String getMimeType() {
        return "application/json";
    }

    @Override
    public String getMutationAttribute() {
        return "_self";
    }

    @Override
    public String getContentAttribute() {
        return "?json";
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

    @Override
    public void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception {
        ObjectWriter objectWriter = new ObjectMapper().writer(new JsonPrettyPrinter());
        String formattedContent = objectWriter.writeValueAsString(content).replace("\r\n", "\n");
        CiteckPsiUtils.setContent(psiFile, formattedContent);
    }
}
