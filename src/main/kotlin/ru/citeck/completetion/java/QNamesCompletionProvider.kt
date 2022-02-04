package ru.citeck.completetion.java

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.util.ProcessingContext
import icons.EcosIcons
import ru.citeck.indexes.qnames.QNamesService
import ru.citeck.metadata.QName
import ru.citeck.metadata.providers.ModelsProvider

class QNamesCompletionProvider : CompletionProvider<CompletionParameters?>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        if (JavaCompletionUtil.getExpectedTypes(parameters)?.any { it.canonicalText == QName.CLASS } != true) {
            return
        }
        val project = parameters.editor.project ?: return

        val models = project.getService(ModelsProvider::class.java).data ?: return
        val namespaces = HashMap<String, String>()
        models.forEach { model ->
            model.namespaces?.forEach { namespace ->
                namespaces[namespace.uri] = namespace.prefix
            }
        }

        val qnames = project.getService(QNamesService::class.java).findAll()

        qnames.forEach {

            val prefix = namespaces[it.uri] ?: return@forEach

            resultSet.addElement(
                LookupElementBuilder
                    .create("${prefix}_${it.localName}")
                    .withIcon(EcosIcons.CiteckLogo)
                    .withTypeText("${it.jClass}.${it.jField}")
                    .withInsertHandler { context, item ->
                        ApplicationManager.getApplication().runWriteAction {
                            CommandProcessor.getInstance().runUndoTransparentAction {
                                context.document.replaceString(
                                    context.startOffset,
                                    context.tailOffset,
                                    "${it.jClass}.${it.jField}"
                                )
                            }
                        }
                    }
            )
        }

    }

}