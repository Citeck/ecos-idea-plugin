package ru.citeck.ecos.files.types.ecos.process;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.types.ecos.XmlEcosArtifact;
import ru.citeck.ecos.files.types.filters.*;
import ru.citeck.ecos.settings.EcosServer;

import java.util.Optional;

public class Cmmn extends XmlEcosArtifact {

    public static final String SOURCE_ID = "eproc/cmmn-def";
    public static final String PATH = "/process/cmmn/";
    public static final String LEGACY_PATH = "/case/templates/";

    private final FileFilter filter = new FilterAnd(
            FileExtensionFilter.XML,
            new FilterOr(
                    new FolderNamePatternsFilter(PATH),
                    new FolderNamePatternsFilter(LEGACY_PATH)
            )
    );

    public Cmmn() {
        super(PATH, SOURCE_ID);
    }

    @Override
    public PsiElement getIdPsiElement(PsiFile psiFile) {
        return psiFile;
    }

    @Override
    public String getId(@NotNull PsiFile psiFile) {
        return Optional
                .of(psiFile)
                .filter(XmlFile.class::isInstance)
                .map(XmlFile.class::cast)
                .map(XmlFile::getRootTag)
                .map(xmlTag -> xmlTag.getAttribute("processDefId", "http://www.citeck.ru/ecos/cmmn/1.0"))
                .map(XmlAttribute::getValueElement)
                .map(XmlAttributeValue::getValue)
                .orElseGet(psiFile::getName);
    }

    @Override
    public String getURL(EcosServer ecosServer, PsiFile psiFile) {

        String targetNamespace = Optional
                .of(psiFile)
                .filter(XmlFile.class::isInstance)
                .map(XmlFile.class::cast)
                .map(XmlFile::getRootTag)
                .map(xmlTag -> xmlTag.getAttribute("targetNamespace"))
                .map(XmlAttribute::getValue)
                .orElse(null);

        if ("http://bpmn.io/schema/cmmn".equals(targetNamespace)) {
            return String.format(
                    "%s/v2/cmmn-editor?recordRef=%s@%s",
                    ecosServer.getHost(),
                    getSourceId(),
                    getId(psiFile)
            );
        }

        return String.format(
                "%s/legacy/cmmn-editor/index.html?templateRef=%s@%s",
                ecosServer.getHost(),
                getSourceId(),
                getId(psiFile)
        );

    }

    @Override
    public FileFilter getFilter() {
        return filter;
    }

    @Override
    public String getDocumentationUrl() {
        return "https://citeck-ecos.readthedocs.io/ru/latest/general/Processes/ECOS_CMMN.html";
    }

}
