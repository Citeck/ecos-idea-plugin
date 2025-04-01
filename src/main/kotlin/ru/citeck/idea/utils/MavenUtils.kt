package ru.citeck.idea.utils

import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.xml.XmlFile
import java.util.*

object MavenUtils {

    fun getPomFile(psiElement: PsiElement): XmlFile? {
        val module = ModuleUtil.findModuleForPsiElement(psiElement)
        return getPomFile(module)
    }

    fun getPomFile(module: Module?): XmlFile? {

        if (module == null) {
            return null
        }

        val moduleDir = module.guessModuleDir() ?: return null

        return Optional
            .ofNullable(moduleDir.findChild("pom.xml"))
            .map { vFile: VirtualFile? ->
                (PsiUtil.getPsiFile(
                    module.project,
                    vFile!!
                ))
            }
            .filter { obj: PsiFile? -> XmlFile::class.java.isInstance(obj) }
            .map { obj: PsiFile? -> XmlFile::class.java.cast(obj) }
            .orElse(null)
    }
}
