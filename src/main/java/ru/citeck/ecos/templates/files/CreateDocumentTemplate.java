package ru.citeck.ecos.templates.files;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.types.ecos.notification.NotificationTemplate;
import ru.citeck.ecos.files.types.ecos.transformation.DocumentTemplate;

import java.util.List;

public class CreateDocumentTemplate extends AbstractCreateEcosArtifactAction {

    public CreateDocumentTemplate() {
        super("Document Template", List.of("meta.yml"), "ecos-document-template", DocumentTemplate.PATH);
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {

        FileTemplate ftlTemplate = FileTemplateManager.getInstance(dir.getProject())
                .findInternalTemplate(getTemplateName() + ".ftl");

        super.createFileFromTemplate(name, ftlTemplate, dir);

        return super.createFileFromTemplate(name, template, dir);
    }

}
