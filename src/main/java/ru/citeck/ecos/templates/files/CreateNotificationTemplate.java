package ru.citeck.ecos.templates.files;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.files.types.ecos.NotificationTemplate;

import java.util.List;

public class CreateNotificationTemplate extends AbstractCreateEcosArtifactAction {

    public CreateNotificationTemplate() {
        super("Notification Template", List.of("html.ftl.meta.yml"), "ecos-notification-template", NotificationTemplate.PATH);
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {

        FileTemplate ftlTemplate = FileTemplateManager.getInstance(dir.getProject())
                .findInternalTemplate(getTemplateName() + ".html.ftl");

        super.createFileFromTemplate(name, ftlTemplate, dir);

        return super.createFileFromTemplate(name, template, dir);
    }

}
