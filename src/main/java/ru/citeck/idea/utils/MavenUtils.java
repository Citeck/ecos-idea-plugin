package ru.citeck.idea.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlFile;

import java.util.Optional;

public class MavenUtils {

    public static XmlFile getPomFile(PsiElement psiElement) {
        Module module = ModuleUtil.findModuleForPsiElement(psiElement);
        return getPomFile(module);
    }

    public static XmlFile getPomFile(Module module) {

        if (module == null) {
            return null;
        }

        VirtualFile moduleDir = ProjectUtil.guessModuleDir(module);
        if (moduleDir == null) {
            return null;
        }

        return Optional
            .ofNullable(moduleDir.findChild("pom.xml"))
            .map(vFile -> (PsiUtil.getPsiFile(module.getProject(), vFile)))
            .filter(XmlFile.class::isInstance)
            .map(XmlFile.class::cast)
            .orElse(null);
    }
}
