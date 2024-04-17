package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.ecos.files.types.filters.FileExtensionFilter;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Optional;

public abstract class YamlEcosArtifact extends AbstractEcosArtifact {
    public YamlEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.YAML, sourceId);
    }

    @Override
    public String getMimeType() {
        return "application/x-yaml";
    }

    @Override
    public String getMutationAttribute() {
        return "_self";
    }

    @Override
    public String getContentAttribute() {
        return ".json|yaml()";
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return Optional
                .ofNullable(YAMLUtil.getValue((YAMLFile) psiFile, "id"))
                .map(psiElementStringPair -> psiElementStringPair.getFirst())
                .orElse(null);
    }

    @Override
    public void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception {
        EcosPsiUtils.setContent(psiFile, content.asText().replace("\r\n", "\n"));
    }

}
