package ru.citeck.ecos.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.settings.EcosServer;

public interface FileFetcher {

    ExtensionPointName<FileFetcher> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.fileFetcher");

    JsonNode fetchContent(EcosServer ecosServer, PsiFile psiFile) throws Exception;

    void applyContent(PsiFile psiFile, JsonNode content) throws Exception;

    boolean canFetch(PsiFile psiFile, FileType fileType);

    String getArtifactName(PsiFile psiFile);

    String getSourceName(EcosServer ecosServer, PsiFile psiFile);

}
