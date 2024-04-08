package ru.citeck.ecos.files.types.ecos;

import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.BrowsableArtifact;
import ru.citeck.ecos.settings.EcosServer;

public interface BrowsableEcosArtifact extends EcosArtifact, BrowsableArtifact {

    @Override
    default String getURL(EcosServer ecosServer, PsiFile psiFile) {
        return String.format(
                "%s/v2/dashboard?recordRef=%s@%s",
                ecosServer.getHost(),
                getSourceId(),
                getId(psiFile)
        );
    }

}
