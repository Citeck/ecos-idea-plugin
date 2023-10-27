package ru.citeck.ecos.files;

import com.intellij.psi.PsiFile;

public interface FileDeployer {

    void deploy(PsiFile psiFile) throws Exception;

    boolean canDeploy(PsiFile psiFile, FileType fileType);

    String getDestinationName(PsiFile psiFile);

}
