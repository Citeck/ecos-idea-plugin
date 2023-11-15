package ru.citeck.ecos.files.deployers;

import com.intellij.psi.PsiFile;

import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.AlfrescoConfig;

public class AlfrescoConfigDeployer extends TomcatFileDeployer {

    @Override
    public void deploy(PsiFile psiFile) throws Exception {
        super.deploy(psiFile);
        ServiceRegistry.getEcosRestApiService().resetShareIndex();
    }

    @Override
    public boolean canDeploy(PsiFile psiFile, FileType fileType) {
        return fileType instanceof AlfrescoConfig;
    }

}
