package ru.citeck.idea.utils

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import java.nio.charset.StandardCharsets

object CiteckPsiUtils {

    fun getRootTag(psiFile: PsiFile): XmlTag? {
        if (psiFile !is XmlFile) {
            return null
        }
        return psiFile.rootTag
    }

    fun getProperties(jsonObject: JsonObject, vararg property: String): Map<String, String> {
        val properties = property.toSet()
        return jsonObject.propertyList
            .filter { properties.contains(it.name) }
            .associate { it.name to getValue(it) }
    }

    fun getProperty(jsonObject: JsonObject, property: String): String {
        return getValue(jsonObject.findProperty(property))
    }

    fun getValue(jsonProperty: JsonProperty?): String {
        if (jsonProperty == null) {
            return ""
        }
        val jsonValue = jsonProperty.value ?: return ""
        val value = jsonValue.text ?: return ""
        return JsonPsiUtil.stripQuotes(value)
    }

    fun setContent(psiFile: PsiFile, content: String) {
        val document = PsiDocumentManager.getInstance(psiFile.project).getDocument(psiFile)
        if (document != null) {
            document.setText(content)
            return
        }
        psiFile.virtualFile.setBinaryContent(content.toByteArray(StandardCharsets.UTF_8))
    }
}
