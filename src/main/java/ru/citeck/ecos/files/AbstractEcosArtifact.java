package ru.citeck.ecos.files;

import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.ecos.files.types.filters.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractEcosArtifact implements EcosArtifact {

    private final FileFilter filter;

    private final Map<Class<? extends PsiFile>, Function<PsiFile, PsiElement>> idProviders = Map.of(
            JsonFile.class, this::getIdPsiElementJson,
            YAMLFile.class, this::getIdPsiElementYaml
    );

    public AbstractEcosArtifact(String folder) {
        filter = new FilterAnd(
                new FilterOr(FileExtensionFilter.JSON, FileExtensionFilter.YAML),
                new FolderNamePatternsFilter(folder)
        );
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return idProviders
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(psiFile))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(idProvider -> idProvider.apply(psiFile))
                .orElseThrow(() -> new RuntimeException("Unable to resolve file type"));
    }

    private PsiElement getIdPsiElementYaml(PsiFile psiFile) {
        return Optional
                .ofNullable(YAMLUtil.getValue((YAMLFile) psiFile, "id"))
                .map(psiElementStringPair -> psiElementStringPair.getFirst())
                .orElse(null);
    }

    private PsiElement getIdPsiElementJson(PsiFile psiFile) {
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
    public FileFilter getFilter() {
        return filter;
    }
}
