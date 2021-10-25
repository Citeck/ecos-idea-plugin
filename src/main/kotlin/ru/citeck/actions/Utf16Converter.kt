package ru.citeck.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import org.apache.commons.lang.StringEscapeUtils
import java.util.function.Function

class Utf16Encoder: Utf16Converter(converter = Function { StringEscapeUtils.escapeJavaScript(it) })
class Utf16Decoder: Utf16Converter(converter = Function { StringEscapeUtils.unescapeJavaScript(it) })

abstract class Utf16Converter(val converter: Function<String, String>) : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val document = editor.document

        val primaryCaret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd

        WriteCommandAction.runWriteCommandAction(project) {
            val selectedText = document.getText(TextRange(start, end))
            val convertedText = converter.apply(selectedText)
            document.replaceString(start, end, convertedText)
        }
        primaryCaret.removeSelection()
    }

}