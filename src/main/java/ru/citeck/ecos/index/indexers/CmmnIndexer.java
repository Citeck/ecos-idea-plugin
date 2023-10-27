package ru.citeck.ecos.index.indexers;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.CmmnProcessDefinition;
import ru.citeck.ecos.index.EcosFileIndexer;
import ru.citeck.ecos.index.Indexes;


public class CmmnIndexer implements EcosFileIndexer {

    private static final String PREFIX = "cmmn";

    @Override
    public boolean accept(FileType fileType) {
        return fileType instanceof CmmnProcessDefinition;
    }

    @Override
    public void map(@NotNull FileContent inputData, Indexes.FileIndexes indexes) {

        PsiFile psiFile = inputData.getPsiFile();
        if (!(psiFile instanceof XmlFile)) {
            return;
        }

        XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
        if (rootTag == null) {
            return;
        }
        XmlTag cmmnCaseTag = rootTag.findFirstSubTag("cmmn:case");
        if (cmmnCaseTag == null) {
            return;
        }

        String caseType = cmmnCaseTag.getAttributeValue("ns8:caseEcosType");
        if (caseType == null) {
            caseType = cmmnCaseTag.getAttributeValue("ns8:caseType");
        }
        if (caseType == null) {
            return;
        }

        if (caseType.startsWith("workspace-SpacesStore-")) {
            caseType = caseType.substring("workspace-SpacesStore-".length());
        }

        indexes
            .addSearchEverywhere(PREFIX + "@" + caseType, cmmnCaseTag)
            .addReference(caseType, cmmnCaseTag);

    }

}
