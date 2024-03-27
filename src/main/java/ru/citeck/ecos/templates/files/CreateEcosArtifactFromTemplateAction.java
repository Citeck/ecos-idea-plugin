package ru.citeck.ecos.templates.files;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import icons.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.utils.EcosMessages;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CreateEcosArtifactFromTemplateAction extends CreateFileFromTemplateAction {

    public static final ExtensionPointName<EcosArtifactTemplate> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.ecosArtifactTemplate");

    public static class YAML extends CreateEcosArtifactFromTemplateAction {
        public YAML() {
            super("yaml");
        }
    }

    public static class JSON extends CreateEcosArtifactFromTemplateAction {
        public JSON() {
            super("json");
        }
    }

    private final String extension;

    public CreateEcosArtifactFromTemplateAction(String extension) {
        super("Ecos Artifact (" + extension + ")", "Create Ecos artifact", Icons.CiteckLogo);
        this.extension = extension;
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {

        FileTemplateManager fileTemplateManager = FileTemplateManager.getDefaultInstance();

        Optional
                .ofNullable(ModuleUtil.findModuleForPsiElement(directory))
                .map(Module::getName)
                .ifPresent(moduleName -> {
                    builder.setTitle(String.format("Ecos Artifact (%s):", moduleName));
                    EP_NAME
                            .extensions()
                            .filter(eTemplate -> {
                                String templateName = eTemplate.getTemplate() + "." + getExtension();
                                return fileTemplateManager.findInternalTemplate(templateName) != null;
                            })
                            .forEach(eTemplate -> builder.addKind(
                                    eTemplate.getKind(),
                                    Icons.CiteckLogo,
                                    eTemplate.getTemplate() + "." + getExtension())
                            );
                });
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull @NonNls String newName, @NonNls String templateName) {
        return "Create Ecos Artifact " + getExtension();
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {

        Project project = dir.getProject();

        Module module = ModuleUtil.findModuleForPsiElement(dir);
        if (module == null) {
            EcosMessages.error("Error", "Cannot determine module for file.", project);
            return null;
        }

        VirtualFile moduleDir = ProjectUtil.guessModuleDir(module);
        if (moduleDir == null) {
            EcosMessages.error("Error", "Cannot determine module directory.", project);
            return null;
        }

        EcosArtifactTemplate eTemplate = EP_NAME
                .extensions()
                .filter(t -> Objects.equals(template.getName(), t.getTemplate()))
                .findFirst()
                .orElse(null);

        String path = String.format(
                "%s/src/main/resources/alfresco/module/%s%s",
                moduleDir.getPath(),
                module.getName(),
                eTemplate.getPath()
        );

        PsiDirectory psiDirectory = Optional
                .ofNullable(EcosVirtualFileUtils.getFileByPath(path))
                .or(() -> {
                    FileUtil.createDirectory(new File(path));
                    VirtualFileManager.getInstance().syncRefresh();
                    return Optional.ofNullable(EcosVirtualFileUtils.getFileByPath(path));
                })
                .map(virtualFile -> PsiManager.getInstance(project).findDirectory(EcosVirtualFileUtils.getFileByPath(path)))
                .orElse(null);

        if (psiDirectory == null) {
            EcosMessages.error("Error", "Cannot get file directory", project);
            return null;
        }

        return super.createFileFromTemplate(name, template, psiDirectory);

    }

    public String getExtension() {
        return extension;
    }

}
