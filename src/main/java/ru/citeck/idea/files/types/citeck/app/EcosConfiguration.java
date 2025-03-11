package ru.citeck.idea.files.types.citeck.app;

import com.intellij.psi.PsiFile;
import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.JsonEcosArtifact;
import ru.citeck.idea.files.types.citeck.YamlEcosArtifact;

public interface EcosConfiguration extends EcosArtifact {

    String SOURCE_ID = "eapps/config";
    String PATH = "/app/config/";

    @Override
    default String getId(PsiFile psiFile) {
        return "app/eapps$" + EcosArtifact.super.getId(psiFile);
    }

    @Override
    default boolean canDeploy(PsiFile psiFile) {
        return false;
    }

    @Override
    default boolean canFetch(PsiFile psiFile) {
        return false;
    }

    class JSON extends JsonEcosArtifact implements EcosConfiguration {
        public JSON() {
            super(PATH, SOURCE_ID);
        }
    }

    class YAML extends YamlEcosArtifact implements EcosConfiguration {
        public YAML() {
            super(PATH, SOURCE_ID);
        }
    }

}
