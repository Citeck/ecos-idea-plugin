package ru.citeck.ecos.files;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;

public interface FileDeployer {

    ExtensionPointName<FileDeployer> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.fileDeployer");

    void deploy(PsiFile psiFile) throws Exception;

    boolean canDeploy(PsiFile psiFile, FileType fileType);

    String getDestinationName(PsiFile psiFile);

}
