package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;

import java.util.List;

public abstract class AbstractEcosArtifactFetcher implements FileFetcher {

    protected abstract String getContentAttribute();
    protected abstract String postprocessContent(JsonNode content) throws Exception;
    protected abstract Class<? extends EcosArtifact> getFileType();

    @Override
    public String fetch(PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        String id = fileType.getId(psiFile);

        JsonNode json = ServiceRegistry
                .getEcosRestApiService()
                .queryRecord(fileType.getSourceId(), id, List.of(getContentAttribute()))
                .get("attributes")
                .get(getContentAttribute());

        return postprocessContent(json);

    }

    @Override
    public boolean canFetch(PsiFile psiFile, FileType fileType) {
        if (!(getFileType().isInstance(fileType))) {
            return false;
        }
        String id = ((EcosArtifact) fileType).getId(psiFile);
        return Strings.isNotEmpty(id);
    }

    @Override
    public String getSourceName(PsiFile psiFile) {
        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        return String.format("%s@%s", fileType.getSourceId(), fileType.getId(psiFile));
    }

}