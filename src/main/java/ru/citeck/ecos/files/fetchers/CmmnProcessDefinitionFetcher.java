package ru.citeck.ecos.files.fetchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileFetcher;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.CmmnProcessDefinition;
import ru.citeck.ecos.rest.EcosRestApiService;
import ru.citeck.ecos.ui.TextInputDialog;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmmnProcessDefinitionFetcher implements FileFetcher {

    private final Map<String, String> lastNodeRefs = new HashMap<>();

    @Override
    public String fetch(PsiFile psiFile) throws Exception {
        Project project = psiFile.getProject();

        String path = psiFile.getVirtualFile().getPath();

        String lastNodeRef = lastNodeRefs.get(path);

        TextInputDialog nodeRefInputDialog = new TextInputDialog(
            "Specify NodeRef with case template:",
            lastNodeRef != null ? lastNodeRef : ""
        );
        if (!nodeRefInputDialog.showAndGet()) {
            throw new RuntimeException("Fetching cancelled");
        }
        String nodeRef = nodeRefInputDialog.getValue();
        lastNodeRefs.put(path, nodeRef);

        EcosRestApiService ecosRestApiService = ServiceRegistry.getEcosRestApiService();

        JsonNode response = ecosRestApiService.execute("/share/proxy/alfresco/citeck/case/template?nodeRef=" + nodeRef, null, 20000);
        String template = response.get("template").textValue();
        String content = ecosRestApiService.queryRecord(null, template, List.of("cm:content?disp"))
            .get("attributes")
            .get("cm:content?disp")
            .asText();

        XmlFile newXmlPsi = (XmlFile) PsiFileFactory
            .getInstance(project)
            .createFileFromText(XMLLanguage.INSTANCE, content);

        XmlTag newCaseTag = newXmlPsi.getRootTag().findFirstSubTag("cmmn:case");
        Arrays.stream(newCaseTag.getAttributes()).forEach(PsiElement::delete);
        Arrays.stream(newCaseTag.getChildren()).forEach(psiElement -> {
            if (psiElement instanceof PsiWhiteSpace) {
                psiElement.delete();
            }
        });


        XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
        XmlTag caseTag = rootTag.findFirstSubTag("cmmn:case");
        Arrays.stream(caseTag.getAttributes())
            .forEach(xmlAttribute -> newCaseTag.setAttribute(xmlAttribute.getName(), xmlAttribute.getValue()));

        return newXmlPsi.getText();
    }

    @Override
    public boolean canFetch(PsiFile psiFile, FileType fileType) {
        return fileType instanceof CmmnProcessDefinition;
    }

    @Override
    public String getSourceName(PsiFile psiFile) {
        return "eproc/procdef";
    }
}
