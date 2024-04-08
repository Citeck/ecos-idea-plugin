package ru.citeck.ecos.files;

import com.intellij.psi.PsiFile;
import ru.citeck.ecos.settings.EcosServer;

public interface BrowsableArtifact {

    String getURL(EcosServer ecosServer, PsiFile psiFile);

}
