package ru.citeck.ecos.files.types.ecos;

import com.intellij.psi.PsiFile;

public interface EcosConfiguration extends EcosArtifact {

    String SOURCE_ID = "eapps/config";
    String PATH = "/app/config/";

    @Override
    default String getId(PsiFile psiFile) {
        return "app/eapps$" + EcosArtifact.super.getId(psiFile);
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

    @Override
    default String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/admin/configuration.html";
    }

    @Override
    default boolean canDeploy(PsiFile psiFile) {
        return false;
    }

    @Override
    default boolean canFetch(PsiFile psiFile) {
        return false;
    }

}
