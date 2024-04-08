package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.settings.EcosServer;

import java.util.List;

public class EcosArtifactFetcher implements FileFetcher {

    @Override
    public String fetch(EcosServer ecosServer, PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        String id = fileType.getId(psiFile);

        JsonNode json = ServiceRegistry
                .getEcosRestApiService(ecosServer, psiFile.getProject())
                .queryRecord(fileType.getSourceId(), id, List.of(fileType.getContentAttribute()))
                .get("attributes")
                .get(fileType.getContentAttribute());

        return fileType.getContentPostprocessor().apply(json);

    }

    @Override
    public boolean canFetch(PsiFile psiFile, FileType fileType) {
        if (!(fileType instanceof EcosArtifact)) {
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
