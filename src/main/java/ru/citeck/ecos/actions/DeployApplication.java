package ru.citeck.ecos.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.FileTypeService;
import ru.citeck.ecos.files.types.ecos.EcosApplication;

import java.util.Arrays;
import java.util.Optional;

public class DeployApplication extends DeployFile {

    @Override
    public PsiFile getPsiFile(AnActionEvent event) {

        PsiElement psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT);
        if (psiElement == null) {
            return null;
        }

        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        if (module == null) {
            return null;
        }

        VirtualFile moduleDir = ProjectUtil.guessModuleDir(module);
        if (moduleDir == null) {
            return null;
        }

        Project project = psiElement.getProject();
        FileTypeService fileTypeService = ServiceRegistry.getFileTypeService();

        return Optional
                .ofNullable(VirtualFileUtil.findDirectory(moduleDir, "." + EcosApplication.PATH))
                .map(dir -> PsiManager.getInstance(project).findDirectory(dir))
                .map(PsiDirectory::getFiles)
                .stream()
                .flatMap(Arrays::stream)
                .filter(psiFile -> fileTypeService.isInstance(psiFile, EcosApplication.class))
                .findFirst()
                .orElse(null);
    }

}
