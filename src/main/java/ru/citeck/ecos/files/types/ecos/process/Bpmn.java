package ru.citeck.ecos.files.types.ecos.process;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import ru.citeck.ecos.files.types.ecos.XmlEcosArtifact;
import ru.citeck.ecos.settings.EcosServer;

import java.util.Optional;

public class Bpmn extends XmlEcosArtifact {

    public static final String SOURCE_ID = "eproc/bpmn-def";
    public static final String PATH = "/process/bpmn/";

    public Bpmn() {
        super(PATH, SOURCE_ID);
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return Optional
                .ofNullable(psiFile)
                .filter(XmlFile.class::isInstance)
                .map(XmlFile.class::cast)
                .map(XmlFile::getRootTag)
                .map(xmlTag -> xmlTag.getAttribute("processDefId", "http://www.citeck.ru/ecos/bpmn/1.0"))
                .map(XmlAttribute::getValueElement)
                .orElse(null);
    }

    @Override
    public String getURL(EcosServer ecosServer, PsiFile psiFile) {
        return String.format(
                "%s/v2/bpmn-editor?recordRef=%s@%s",
                ecosServer.getHost(),
                getSourceId(),
                getId(psiFile)
        );
    }
    @Override
    public String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/settings_kb/processes/BPMN_ecos.html";
    }

}
