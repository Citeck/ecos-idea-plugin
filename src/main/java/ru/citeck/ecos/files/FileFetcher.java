package ru.citeck.ecos.files;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;

public interface FileFetcher {

    ExtensionPointName<FileFetcher> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.fileFetcher");

    String fetch(PsiFile psiFile) throws Exception;

    boolean canFetch(PsiFile psiFile, FileType fileType);

    String getSourceName(PsiFile psiFile);

}
