package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.settings.EcosServer;

import java.util.List;

public class EcosArtifactFetcher implements FileFetcher {

    @Override
    public JsonNode fetchContent(EcosServer ecosServer, PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        String id = fileType.getId(psiFile);

        return ServiceRegistry
                .getEcosRestApiService(ecosServer, psiFile.getProject())
                .queryRecord(fileType.getSourceId(), id, List.of(fileType.getContentAttribute()))
                .get("attributes")
                .get(fileType.getContentAttribute());

    }

    @Override
    public void applyContent(PsiFile psiFile, JsonNode content) throws Exception {
        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        fileType.applyFetchedContent(psiFile, content);
    }

    @Override
    public boolean canFetch(PsiFile psiFile, FileType fileType) {
        if (!(fileType instanceof EcosArtifact)) {
            return false;
        }
        return ((EcosArtifact) fileType).canFetch(psiFile);
    }

    @Override
    public String getArtifactName(PsiFile psiFile) {
        return ((EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile)).getFullId(psiFile);
    }

    @Override
    public String getSourceName(EcosServer ecosServer, PsiFile psiFile) {
        return ecosServer.getHost();
    }

}
