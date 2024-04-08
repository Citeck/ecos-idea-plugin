package ru.citeck.ecos.files.deployers;

import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.AlfrescoConfig;
import ru.citeck.ecos.settings.EcosServer;

public class AlfrescoConfigDeployer extends TomcatFileDeployer {

    @Override
    public void deploy(EcosServer ecosServer, PsiFile psiFile) throws Exception {
        super.deploy(ecosServer, psiFile);
        ServiceRegistry.getEcosRestApiService(ecosServer, psiFile.getProject()).resetShareIndex();
    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        return fileType instanceof AlfrescoConfig;
    }

}
