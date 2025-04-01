package ru.citeck.idea.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.apache.commons.lang3.exception.ExceptionUtils
import ru.citeck.idea.exceptions.GracefulAbortException
import ru.citeck.idea.project.CiteckProject
import ru.citeck.idea.utils.CiteckMessages

abstract class CiteckFileAction : AnAction(), DumbAware {

    companion object {
        private val EXCEPTIONS_WITHOUT_TYPE_IN_ERROR_MESSAGE: Set<Class<*>> = setOf(
            RuntimeException::class.java,
            IllegalStateException::class.java
        )

        private val log = Logger.getInstance(CiteckFileAction::class.java)
    }

    override fun update(event: AnActionEvent) {
        event.presentation.isEnabledAndVisible = isActionAllowedInternal(event)
    }

    private fun isActionAllowedInternal(event: AnActionEvent): Boolean {

        val project = event.project ?: return false
        val psiFile = getPsiFile(event) ?: return false

        return CiteckProject.getInstance(project).isCiteckModule(ModuleUtil.findModuleForPsiElement(psiFile))
            && isActionAllowed(event, psiFile, project)
    }

    abstract fun isActionAllowed(event: AnActionEvent, file: PsiFile, project: Project): Boolean

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    protected abstract fun perform(event: AnActionEvent)

    override fun actionPerformed(event: AnActionEvent) {
        if (!isActionAllowedInternal(event)) {
            log.debug(
                "Action is disabled, but actionPerformed " +
                "was called for ${getPsiFile(event)?.virtualFile?.path}"
            )
            return
        }
        try {
            perform(event)
        } catch (e: Exception) {
            var rootCause = ExceptionUtils.getRootCause(e)
            if (rootCause == null) {
                rootCause = e
            }
            if (rootCause is GracefulAbortException) {
                log.debug("GracefulAbortException", e.message)
            } else {
                log.error("Action Error", e)
                var errorMsg: String? = ""
                if (!EXCEPTIONS_WITHOUT_TYPE_IN_ERROR_MESSAGE.contains(rootCause.javaClass)) {
                    errorMsg += rootCause.javaClass.simpleName + ": "
                }
                errorMsg += rootCause.message
                CiteckMessages.error("Action error", errorMsg, event.project)
            }
        }
    }

    open fun getPsiFile(event: AnActionEvent): PsiFile? {
        return event.getData(PlatformDataKeys.PSI_FILE)
    }
}
