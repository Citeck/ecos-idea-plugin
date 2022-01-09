package ru.citeck.completetion.java

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.JavaCompletionUtil
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ProcessingContext
import icons.EcosIcons
import org.jetbrains.annotations.NotNull
import ru.citeck.metadata.QName
import ru.citeck.metadata.providers.QNamesProvider

class QNamesCompletionProvider : CompletionProvider<CompletionParameters?>() {

    override fun addCompletions(
        parameters: @NotNull CompletionParameters,
        context: @NotNull ProcessingContext,
        resultSet: @NotNull CompletionResultSet
    ) {
        if (JavaCompletionUtil.getExpectedTypes(parameters)?.any { it.canonicalText == QName.CLASS } != true) {
            return
        }
        val project = parameters.editor.project ?: return

        val qnames = project.getService(QNamesProvider::class.java).getData() ?: return


        val lookup = LookupElementBuilder.create("qname").withIcon(EcosIcons.CiteckLogo)
            .withInsertHandler { ctx, item ->
                JBPopupFactory.getInstance()
                    .createPopupChooserBuilder(qnames)
                    .setTitle("Select QName:")

                    .setItemChosenCallback {
                        ApplicationManager.getApplication().runWriteAction {
                            CommandProcessor.getInstance().runUndoTransparentAction {
                                ctx.document.replaceString(
                                    ctx.startOffset,
                                    ctx.tailOffset,
                                    "${it.javaClass}.${it.javaField}"
                                )
                            }
                        }
                    }
                    .setNamerForFiltering { it.toString() }
                    .setRequestFocus(true).createPopup()
                    .show(
                        RelativePoint.getCenterOf(WindowManager.getInstance().getFrame(project)!!.rootPane)
                    )
            }
        resultSet.addElement(lookup)

    }

}