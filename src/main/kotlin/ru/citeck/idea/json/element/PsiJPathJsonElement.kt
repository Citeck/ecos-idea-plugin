package ru.citeck.idea.json.element

import com.intellij.json.psi.*
import com.intellij.psi.PsiElement
import kotlin.math.min

class PsiJPathJsonElement(
    private val element: JsonValue
): PsiJPathElement {

    companion object {
        fun fromFile(file: JsonFile?): PsiJPathJsonElement? {
            return file?.topLevelValue?.let { PsiJPathJsonElement(it) } ?: return null
        }
    }

    override fun getByKey(key: String): PsiJPathElement? {
        if (element !is JsonObject) {
            return null
        }
        val value = element.findProperty(key)?.value ?: return null
        return PsiJPathJsonElement(value)
    }

    override fun getChildren(from: Int, to: Int): List<PsiJPathElement> {
        if (element !is JsonArray) {
            return emptyList()
        }
        val elements = element.valueList
        val result = ArrayList<PsiJPathElement>()

        var fromIdx = from - 1
        val toIdx = min(elements.size, to)
        while (++fromIdx < toIdx) {
            result.add(PsiJPathJsonElement(elements[fromIdx]))
        }

        return result
    }

    override fun asText(): String {
        return JsonPsiUtil.stripQuotes(element.text)
    }

    override fun getPsiElement(): PsiElement {
        return element
    }
}
/*        private fun getItems(model: JsonObject, key: String): Stream<JsonProperty> {
            return Optional
                .ofNullable(model.findProperty(key))
                .map { obj: JsonProperty -> obj.value }
                .map(CommonUtils.filterAndCast(JsonArray::class.java))
                .map { obj: JsonArray -> obj.valueList }
                .stream()
                .flatMap { obj: List<JsonValue> -> obj.stream() }
                .map(CommonUtils.filterAndCast(JsonObject::class.java))
                .map { jsonObject: JsonObject -> jsonObject.findProperty("id") }
        }*/
