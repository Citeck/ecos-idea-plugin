package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.utils.JsonPrettyPrinter;

import java.util.List;
import java.util.Map;

public class EcosArtifactFetcher implements FileFetcher {

    private final Map<Class<? extends PsiFile>, ObjectWriter> writers = Map.of(
            JsonFile.class, new ObjectMapper().writer(new JsonPrettyPrinter()),
            YAMLFile.class, new ObjectMapper(new YAMLFactory()).writer()
    );

    @Override
    public String fetch(PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        String id = fileType.getId(psiFile);

        JsonNode json = ServiceRegistry
                .getEcosRestApiService()
                .queryRecord(fileType.getSourceId(), id, List.of(".json"))
                .get("attributes")
                .get(".json");

        return getWriter(psiFile)
                .writeValueAsString(json)
                .replace("\r\n", "\n");

    }

    @NotNull
    private ObjectWriter getWriter(PsiFile psiFile) {
        return writers
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(psiFile))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to resolve file type"));
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
