package ru.citeck.ecos.files.deployers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.util.text.Strings;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.rest.EcosRestApiService;

import java.util.Map;
import java.util.function.Function;

public class EcosArtifactDeployer implements FileDeployer {

    private final ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
    private final ObjectMapper jsonWriter = new ObjectMapper();

    private final Map<Class<? extends PsiFile>, Function<PsiFile, String>> jsonProviders = Map.of(
            JsonFile.class, PsiElement::getText,
            YAMLFile.class, this::convertYamlToJson
    );

    @Override
    public void deploy(PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        VirtualFile vFile = psiFile.getVirtualFile();

        String sourceId = fileType.getSourceId();
        String id = fileType.getId(psiFile);

        EcosRestApiService ecosRestApiService = ServiceRegistry.getEcosRestApiService();
        boolean recordExists = ecosRestApiService.recordExists(sourceId, id);

        String text = getText(psiFile);

        ecosRestApiService.mutateRecord(
                fileType.getSourceId(),
                recordExists ? id : "",
                "application/json",
                vFile.getName(),
                text.getBytes(vFile.getCharset())
        );

    }

    private String getText(PsiFile psiFile) {
        return jsonProviders
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().isInstance(psiFile))
                .map(Map.Entry::getValue)
                .findFirst()
                .map(textProvider -> textProvider.apply(psiFile))
                .orElseThrow(() -> new RuntimeException("Unable to resolve file type"));
    }

    private String convertYamlToJson(PsiFile psiFile) {
        try {
            Object obj = yamlReader.readValue(psiFile.getText(), Object.class);
            return jsonWriter.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        if (!(fileType instanceof EcosArtifact)) {
            return false;
        }
        String id = ((EcosArtifact) fileType).getId(psiFile);
        return Strings.isNotEmpty(id);
    }

    @Override
    public String getDestinationName(PsiFile psiFile) {
        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        return String.format("%s@%s", fileType.getSourceId(), fileType.getId(psiFile));
    }

}
