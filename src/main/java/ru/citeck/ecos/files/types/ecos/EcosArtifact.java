package ru.citeck.ecos.files.types.ecos;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.HasDocumentation;

import java.util.Collections;
import java.util.List;

public interface EcosArtifact extends FileType, HasDocumentation {

    String getSourceId();

    PsiElement getIdPsiElement(PsiFile psiFile);

    String getMimeType();

    String getMutationAttribute();

    String getContentAttribute();

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

    default boolean canDeploy(PsiFile psiFile) {
        return Strings.isNotEmpty(getId(psiFile));
    }

    default boolean canFetch(PsiFile psiFile) {
        return Strings.isNotEmpty(getId(psiFile));
    }

    default boolean isIndexable(PsiFile psiFile) {
        return true;
    }

    void applyFetchedContent(PsiFile psiFile, JsonNode content) throws Exception;

}
