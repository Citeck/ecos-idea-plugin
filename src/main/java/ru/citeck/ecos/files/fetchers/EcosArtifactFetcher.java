package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.EcosArtifact;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.utils.JsonPrettyPrinter;

import java.util.List;

public class EcosArtifactFetcher implements FileFetcher {

    private final ObjectWriter objectWriter = new ObjectMapper().writer(new JsonPrettyPrinter());

    @Override
    public String fetch(PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        String id = fileType.getId(psiFile);

        JsonNode json = ServiceRegistry
            .getEcosRestApiService()
            .queryRecord(fileType.getSourceId(), id, List.of(".json"))
            .get("attributes")
            .get(".json");

        return objectWriter
            .writeValueAsString(json)
            .replace("\r\n", "\n");

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
