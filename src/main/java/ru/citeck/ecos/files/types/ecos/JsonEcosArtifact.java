package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.utils.EcosPsiUtils;
import ru.citeck.ecos.utils.JsonPrettyPrinter;

import java.util.function.Function;

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
        return ".json";
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
        EcosPsiUtils.setContent(psiFile, formattedContent);
    }

}
