package ru.citeck.idea.files.types.citeck;

import ecos.com.fasterxml.jackson210.databind.JsonNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.idea.files.types.filters.FileExtensionFilter;
import ru.citeck.idea.utils.CiteckPsiUtils;

import java.util.Optional;

public abstract class YamlEcosArtifact extends AbstractEcosArtifact {

    public YamlEcosArtifact(String path, String sourceId) {
        super(path, FileExtensionFilter.YAML, sourceId);
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return Optional
            .ofNullable(YAMLUtil.getValue((YAMLFile) psiFile, "id"))
            .map(psiElementStringPair -> psiElementStringPair.getFirst())
            .orElse(null);
    }
}
