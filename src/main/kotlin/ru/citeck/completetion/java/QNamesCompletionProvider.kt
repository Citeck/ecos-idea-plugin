package ru.citeck.completetion.java

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import icons.EcosIcons
import ru.citeck.indexes.models.AlfNamespaceIndex
import ru.citeck.indexes.qnames.QNamesService
import ru.citeck.alfresco.QName

class QNamesCompletionProvider : CompletionProvider<CompletionParameters?>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {

        val project = parameters.editor.project ?: return
        val qNamePattern = PsiType.getTypeByName(QName.QNAME_PATTERN, project, GlobalSearchScope.allScope(project))

        if (JavaCompletionUtil.getExpectedTypes(parameters)?.any { qNamePattern.isAssignableFrom(it) } != true) {
            return
        }

        val qnames = project.getService(QNamesService::class.java).findAll()

        val fileBasedIndex = FileBasedIndex.getInstance()
        val searchScope = GlobalSearchScope.allScope(project)
        val namespaces = mutableMapOf<String, String>()

        qnames.forEach {

            var prefix = namespaces[it.uri]
            if (prefix == null) {
                prefix = fileBasedIndex.getValues(AlfNamespaceIndex.NAME, it.uri, searchScope).firstOrNull()?.prefix
                if (prefix == null) return@forEach
                namespaces[it.uri] = prefix
            }

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