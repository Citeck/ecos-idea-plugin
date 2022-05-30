package ru.citeck.actions

import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiJavaFile
import com.intellij.task.ProjectTaskManager
import org.apache.commons.lang.StringEscapeUtils
import ru.citeck.common.EcosServer

class BuildAndReloadJavaClass : HotReloadJavaAction() {
    override fun perform(
        psiFile: PsiJavaFile,
        projectTaskManager: ProjectTaskManager,
        virtualFile: VirtualFile,
        project: Project,
        classPath: String,
        className: String
    ) {
        projectTaskManager.compile(virtualFile)
            .onSuccess {
                runClassReloaderMethods(
                    ClassReloaderMethod("reload", listOf(className, classPath))
                )
            }
    }
}

class RunJavaMethod : HotReloadJavaAction() {

    override fun perform(
        psiFile: PsiJavaFile,
        projectTaskManager: ProjectTaskManager,
        virtualFile: VirtualFile,
        project: Project,
        classPath: String,
        className: String
    ) {
        val methods = getJavaMethods(psiFile)
        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(methods)
            .setTitle("Select method to run:")
            .setItemChosenCallback { method ->
                projectTaskManager.compile(virtualFile)
                    .onSuccess {
                        runClassReloaderMethods(
                            ClassReloaderMethod("reload", listOf(className, classPath)),
                            ClassReloaderMethod("invoke", listOf(className, method))
                        )
                    }
            }
            .setRequestFocus(true).createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isVisible = false
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE) ?: return
        if (psiFile !is PsiJavaFile) return
        event.presentation.isVisible = getJavaMethods(psiFile).isNotEmpty()
        super.update(event)
    }

    private fun getJavaMethods(psiFile: PsiJavaFile): List<String> {
        return psiFile.classes.get(0).methods.filter { psiMethod ->
            !psiMethod.isConstructor && !psiMethod.hasParameters() && !psiMethod.hasModifier(JvmModifier.STATIC)
        }.map { psiMethod -> psiMethod.name }.toList()
    }

}

abstract class HotReloadJavaAction : AnAction() {

    data class ClassReloaderMethod(val name: String, val arguments: List<String>)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        val psiFile = event.getData(PlatformDataKeys.PSI_FILE) as PsiJavaFile
        val module = ProjectFileIndex.SERVICE.getInstance(project).getModuleForFile(virtualFile, false) ?: return
        val classPath = module.guessModuleDir()!!.canonicalPath + "/target/classes/"
        val className = psiFile.packageName + "." + psiFile.classes.get(0).name
        val projectTaskManager = ProjectTaskManager.getInstance(project)

        perform(psiFile, projectTaskManager, virtualFile, project, classPath, className)

    }

    abstract fun perform(
        psiFile: PsiJavaFile,
        projectTaskManager: ProjectTaskManager,
        virtualFile: VirtualFile,
        project: Project,
        classPath: String,
        className: String
    )

    fun runClassReloaderMethods(vararg methods: ClassReloaderMethod) {

        val methodsStr = methods.joinToString(separator = ";\n", transform = {
            val args =
                it.arguments.joinToString(separator = ", ", transform = { "\"${StringEscapeUtils.escapeJavaScript(it)}\"" })
            "reloader.${it.name}(${args});"
        })

        val script =
            "var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();\n" +
                    "reloader = ctx.getBean(Packages.ru.citeck.ecos.utils.devtools.EcosBeanReloader);\n" +
                    methodsStr

        runBackgroundableTask("Reloading java class") {
            EcosServer.current().executeJs(script)
        }
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isVisible = event.getData(PlatformDataKeys.PSI_FILE) is PsiJavaFile
    }
}