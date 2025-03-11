package ru.citeck.idea.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import ru.citeck.idea.actions.file.artifact.DeployArtifact
import ru.citeck.idea.files.FileTypeService
import ru.citeck.idea.files.types.citeck.app.EcosApplication
import java.util.*

class DeployApplication : DeployArtifact() {

    override fun getPsiFile(event: AnActionEvent): PsiFile? {

        val psiElement = event.getData(PlatformDataKeys.PSI_ELEMENT) ?: return null

        val module = ModuleUtil.findModuleForPsiElement(psiElement) ?: return null

        val moduleDir = module.guessModuleDir() ?: return null

        val project = psiElement.project
        val fileTypeService = FileTypeService.getInstance()

        return Optional
            .ofNullable(VirtualFileManager.getInstance().findFileByUrl(moduleDir.url + EcosApplication.PATH))
            .map { dir: VirtualFile? ->
                PsiManager.getInstance(project).findDirectory(
                    dir!!
                )
            }
            .map { obj: PsiDirectory? -> obj!!.files }
            .stream()
            .flatMap { array: Array<PsiFile>? -> Arrays.stream(array) }
            .filter { psiFile: PsiFile? -> fileTypeService.isInstance(psiFile, EcosApplication::class.java) }
            .findFirst()
            .orElse(null)
    }
}
