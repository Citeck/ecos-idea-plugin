package ru.citeck.ecos.files.types.ecos;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;

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
}
