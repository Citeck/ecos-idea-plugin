package ru.citeck.ecos.files;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.settings.EcosServer;

public interface FileDeployer {

    ExtensionPointName<FileDeployer> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.fileDeployer");

    void deploy(EcosServer ecosServer, PsiFile psiFile) throws Exception;

    boolean canDeploy(PsiFile psiFile, FileType fileType);


    String getArtifactName(PsiFile psiFile);

    String getDestinationName(EcosServer ecosServer, PsiFile psiFile);

}
