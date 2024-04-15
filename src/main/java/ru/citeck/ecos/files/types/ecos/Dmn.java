package ru.citeck.ecos.files.types.ecos;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import ru.citeck.ecos.settings.EcosServer;

import java.util.Optional;

public class Dmn extends XmlEcosArtifact {

    public static final String SOURCE_ID = "eproc/dmn-def";
    public static final String PATH = "/process/dmn/";

    public Dmn() {
        super(PATH, SOURCE_ID);
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return Optional
                .ofNullable(psiFile)
                .filter(XmlFile.class::isInstance)
                .map(XmlFile.class::cast)
                .map(XmlFile::getRootTag)
                .map(xmlTag -> xmlTag.getAttribute("defId", "http://www.citeck.ru/ecos/dmn/1.0"))
                .map(XmlAttribute::getValueElement)
                .orElse(null);
    }

    @Override
    public String getURL(EcosServer ecosServer, PsiFile psiFile) {
        return String.format(
                "%s/v2/dmn-editor?recordRef=%s@%s",
                ecosServer.getHost(),
                getSourceId(),
                getId(psiFile)
        );
    }

}
