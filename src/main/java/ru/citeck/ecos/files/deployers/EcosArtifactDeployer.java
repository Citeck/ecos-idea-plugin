package ru.citeck.ecos.files.deployers;

import com.intellij.openapi.util.text.Strings;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileDeployer;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.EcosArtifact;
import ru.citeck.ecos.rest.EcosRestApiService;
import ru.citeck.ecos.settings.EcosServer;

public class EcosArtifactDeployer implements FileDeployer {

    @Override
    public void deploy(EcosServer ecosServer, PsiFile psiFile) throws Exception {

        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        VirtualFile vFile = psiFile.getVirtualFile();

        String sourceId = fileType.getSourceId();
        String id = fileType.getId(psiFile);

        EcosRestApiService ecosRestApiService = ServiceRegistry.getEcosRestApiService(ecosServer, psiFile.getProject());
        boolean recordExists = ecosRestApiService.recordExists(sourceId, id);

        ecosRestApiService.mutateRecord(
                fileType.getSourceId(),
                recordExists ? id : "",
                fileType.getMimeType(),
                vFile.getName(),
                psiFile.getText().getBytes(vFile.getCharset()),
                fileType.getMutationAttribute()
        );

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
    public String getDestinationName(EcosServer ecosServer, PsiFile psiFile) {
        EcosArtifact fileType = (EcosArtifact) ServiceRegistry.getFileTypeService().getFileType(psiFile);
        return String.format("%s@%s", fileType.getSourceId(), fileType.getId(psiFile));
    }

}