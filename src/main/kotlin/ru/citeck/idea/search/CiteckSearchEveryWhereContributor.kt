package ru.citeck.idea.search

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.NameUtil
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.Processor
import org.jetbrains.annotations.Nls
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.IndexValue
import ru.citeck.idea.search.index.IndexesService
import ru.citeck.idea.utils.CiteckVirtualFileUtils
import javax.swing.JList
import javax.swing.ListCellRenderer

class CiteckSearchEveryWhereContributor(
    private val project: Project?
) : SearchEverywhereContributor<IndexValue> {

    companion object {
        private const val MODULE_NAME = "moduleName"
    }

    private val cellRenderer: ListCellRenderer<IndexValue> = object : ColoredListCellRenderer<IndexValue>() {
        override fun customizeCellRenderer(
            list: JList<out IndexValue>,
            value: IndexValue,
            index: Int,
            selected: Boolean,
            hasFocus: Boolean
        ) {
            setPaintFocusBorder(false)
            icon = value.getIcon()
            font = list.font
            append(value.id, SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.foreground), true)

            val moduleName = value.getProperty(MODULE_NAME)
            if (moduleName != null) {
                append(" $moduleName", SimpleTextAttributes.GRAY_SMALL_ATTRIBUTES)
            }
        }
    }

    private fun getJarName(indexValue: IndexValue): String? {
        val fileName = indexValue.file ?: return null
        if (!fileName.contains("!")) {
            return null
        }
        return fileName.split("!")
            .find { it.endsWith(".jar", true) }
            ?.let { CiteckVirtualFileUtils.getFileByPath(it) }
            ?.name
    }

    private fun getModuleName(indexValue: IndexValue): String? {
        project ?: return null
        val fileName = indexValue.file
        val virtualFile = CiteckVirtualFileUtils.getFileByPath(fileName) ?: return null
        return ModuleUtil.findModuleForFile(virtualFile, project)?.name ?: getJarName(indexValue)
    }

    override fun isShownInSeparateTab(): Boolean {
        return true
    }

    override fun processSelectedItem(selected: IndexValue, modifiers: Int, searchText: String): Boolean {
        project ?: return false
        val virtualFile = CiteckVirtualFileUtils.getFileByPath(selected.file) ?: return false
        val descriptor = OpenFileDescriptor(project, virtualFile)
        if (selected.offset == 0) {
            descriptor.navigate(true)
        } else {
            val editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true) ?: return false
            editor.caretModel.moveToOffset(selected.offset)
            editor.scrollingModel.scrollToCaret(ScrollType.CENTER_UP)
        }
        return true
    }

    override fun isMultiSelectionSupported(): Boolean {
        return true
    }

    override fun getSearchProviderId(): String {
        return javaClass.simpleName
    }

    @Nls
    override fun getGroupName(): String {
        return "Citeck"
    }

    override fun getSortWeight(): Int {
        return 0
    }

    override fun showInFindResults(): Boolean {
        return false
    }

    override fun fetchElements(
        pattern: String,
        progressIndicator: ProgressIndicator,
        consumer: Processor<in IndexValue>
    ) {
        if (project == null || DumbService.isDumb(project)) {
            return
        }
        ApplicationManager
            .getApplication()
            .runReadAction {
                val matcher =
                    NameUtil.buildMatcher("*$pattern").build()
                IndexesService.getInstance(project)
                    .stream(IndexKey.SEARCH_EVERYWHERE)
                    .filter { indexValue: IndexValue -> matcher.matches(indexValue.id) }
                    .limit(100)
                    .peek { indexValue: IndexValue ->
                        val moduleName = getModuleName(indexValue)
                        if (moduleName != null) {
                            indexValue.setProperty(MODULE_NAME, moduleName)
                        }
                    }
                    .forEach { t: IndexValue -> consumer.process(t) }
            }
    }

    override fun getElementsRenderer(): ListCellRenderer<IndexValue> {
        return cellRenderer
    }

    override fun getDataForItem(element: IndexValue, dataId: String): Any? {
        return null
    }

    class Factory : SearchEverywhereContributorFactory<IndexValue> {
        override fun createContributor(event: AnActionEvent): SearchEverywhereContributor<IndexValue> {
            return CiteckSearchEveryWhereContributor(event.project)
        }
    }
}
