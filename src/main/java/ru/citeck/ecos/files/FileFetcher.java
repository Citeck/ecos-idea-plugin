package ru.citeck.ecos.files;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.settings.EcosServer;

public interface FileFetcher {

    ExtensionPointName<FileFetcher> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.fileFetcher");

    String fetch(EcosServer ecosServer, PsiFile psiFile) throws Exception;

    boolean canFetch(PsiFile psiFile, FileType fileType);

    String getSourceName(PsiFile psiFile);

}
