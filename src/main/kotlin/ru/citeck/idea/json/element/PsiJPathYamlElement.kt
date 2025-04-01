package ru.citeck.idea.json.element

import com.intellij.psi.PsiElement
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLSequence
import org.jetbrains.yaml.psi.YAMLValue
import kotlin.math.min

class PsiJPathYamlElement(
    private val element: YAMLValue
): PsiJPathElement {

    companion object {

        fun fromFile(psiFile: YAMLFile?): PsiJPathYamlElement? {
            return psiFile?.documents?.firstOrNull()?.topLevelValue?.let { PsiJPathYamlElement(it) }
        }
    }

    override fun getByKey(key: String): PsiJPathElement? {
        if (element !is YAMLMapping) {
            return null
        }
        val valueByKey = element.getKeyValueByKey(key)?.value ?: return null

        return PsiJPathYamlElement(valueByKey)
    }

    override fun getChildren(from: Int, to: Int): List<PsiJPathElement> {
        if (element !is YAMLSequence) {
            return emptyList()
        }

        val elements = element.items
        val result = ArrayList<PsiJPathElement>()

        var fromIdx = from - 1
        val toIdx = min(elements.size, to)
        while (++fromIdx < toIdx) {
            val itemValue = elements[fromIdx].value
            if (itemValue != null) {
                result.add(PsiJPathYamlElement(itemValue))
            }
        }
        return result
    }

    override fun asText(): String {
        return element.text
    }

    override fun getPsiElement(): PsiElement {
        return element
    }
}
/*                fun getItems(yamlFile: YAMLFile, key: String): Stream<YAMLKeyValue> {
            return Optional
                .ofNullable(YAMLUtil.getQualifiedKeyInFile(yamlFile, "model", key))
                .map { obj: YAMLKeyValue -> obj.value }
                .map(CommonUtils.filterAndCast(YAMLSequence::class.java))
                .map { obj: YAMLSequence -> obj.items }
                .stream()
                .flatMap { obj: List<YAMLSequenceItem> -> obj.stream() }
                .map { obj: YAMLSequenceItem -> obj.keysValues }
                .flatMap { obj: Collection<YAMLKeyValue> -> obj.stream() }
                .filter { keyValue: YAMLKeyValue -> "id" == keyValue.keyText }
        }*/
