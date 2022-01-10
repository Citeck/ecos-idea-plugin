package ru.citeck.completetion.java

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
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


        val lookupProperty = LookupElementBuilder.create("").withPresentableText("QName property").withIcon(EcosIcons.CiteckLogo)
            .withInsertHandler { context, item ->
                qNameInsertHandler(
                    project,
                    qnames.filter { it.jField.startsWith("PROP_") }.toList(),
                    context,
                    item
                )
            }

        val lookupAssoc = LookupElementBuilder.create("").withPresentableText("QName association").withIcon(EcosIcons.CiteckLogo)
            .withInsertHandler { context, item ->
                qNameInsertHandler(
                    project,
                    qnames.filter { it.jField.startsWith("ASSOC_") }.toList(),
                    context,
                    item
                )
            }

        resultSet.addElement(lookupProperty)
        resultSet.addElement(lookupAssoc)

    }

    private fun qNameInsertHandler(project: Project, qnames: List<QName>, ctx: InsertionContext, item: LookupElement) {
        JBPopupFactory.getInstance()
            .createPopupChooserBuilder(qnames)
            .setTitle("Select QName:")
            .setRenderer(QNameListCellRenderer())
            .setItemChosenCallback {
                ApplicationManager.getApplication().runWriteAction {
                    CommandProcessor.getInstance().runUndoTransparentAction {
                        ctx.document.replaceString(
                            ctx.startOffset,
                            ctx.tailOffset,
                            "${it.jClass}.${it.jField}"
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


}