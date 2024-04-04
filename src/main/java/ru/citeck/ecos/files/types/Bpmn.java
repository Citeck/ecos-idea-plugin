package ru.citeck.ecos.files.types;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlFile;
import ru.citeck.ecos.files.AbstractEcosArtifact;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Bpmn extends AbstractEcosArtifact {

    public static final String SOURCE_ID = "eproc/bpmn-def";
    public static final String PATH = "/process/bpmn/";

    private static final Map<Class<? extends PsiFile>, Function<PsiFile, PsiElement>> ID_PSI_ELEMENT_PROVIDERS = Map.of(
            XmlFile.class, psiFile -> Optional
                    .ofNullable(psiFile)
                    .filter(XmlFile.class::isInstance)
                    .map(XmlFile.class::cast)
                    .map(XmlFile::getRootTag)
                    .map(xmlTag -> xmlTag.getAttribute("processDefId", "http://www.citeck.ru/ecos/bpmn/1.0"))
                    .map(XmlAttribute::getValueElement)
                    .orElse(null)
    );

    public Bpmn() {
        super(PATH);
    }

    @Override
    public String getSourceId() {
        return SOURCE_ID;
    }

    @Override
    public Map<Class<? extends PsiFile>, Function<PsiFile, PsiElement>> getIdPsiElementProviders() {
        return ID_PSI_ELEMENT_PROVIDERS;
    }

}
