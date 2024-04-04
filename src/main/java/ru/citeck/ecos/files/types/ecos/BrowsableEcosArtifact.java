package ru.citeck.ecos.files.types.ecos;

import com.intellij.psi.PsiFile;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.BrowsableArtifact;

public interface BrowsableEcosArtifact extends EcosArtifact, BrowsableArtifact {

    @Override
    default String getURL(PsiFile psiFile) {
        return String.format(
                "%s/v2/dashboard?recordRef=%s@%s",
                ServiceRegistry.getEcosRestApiService().getHost(),
                getSourceId(),
                getId(psiFile)
        );
    }

}
