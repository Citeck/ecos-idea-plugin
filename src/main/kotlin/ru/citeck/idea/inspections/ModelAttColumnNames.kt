package ru.citeck.idea.inspections

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.json.psi.JsonStringLiteral
import com.intellij.json.psi.JsonValue
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YAMLScalar
import org.jetbrains.yaml.psi.YAMLValue
import ru.citeck.idea.artifacts.ArtifactTypes
import ru.citeck.idea.json.PsiJPath
import ru.citeck.idea.json.element.PsiJPathElement
import ru.citeck.idea.json.element.PsiJPathJsonElement
import ru.citeck.idea.json.element.PsiJPathYamlElement

/**
 * A database column an attribute of a model artifact will be stored in.
 *
 * @param name column name as the storage layer will use it
 * @param idElement PSI element holding the attribute id, to report problems on
 * @param aspect whether the attribute belongs to an aspect (its column name carries the aspect prefix)
 */
data class ModelAttColumn(
    val name: String,
    val idElement: PsiElement,
    val aspect: Boolean
)

/**
 * Computes column names of attributes declared in `model/type` and `model/aspect` artifacts
 * the same way the ECOS storage layer does: the column name is the attribute id, and for an aspect
 * the id is prefixed with the aspect prefix (or the aspect id when the prefix is blank).
 */
object ModelAttColumnNames {

    private val ASPECT_ID = PsiJPath.parse("$.id")
    private val ASPECT_PREFIX = PsiJPath.parse("$.prefix")
    private val ASPECT_ATTS = PsiJPath.parse("$.attributes[:].id")
    private val ASPECT_SYS_ATTS = PsiJPath.parse("$.systemAttributes[:].id")
    private val TYPE_ATTS = PsiJPath.parse("$.model.attributes[:].id")
    private val TYPE_SYS_ATTS = PsiJPath.parse("$.model.systemAttributes[:].id")

    // Mirrors ru.citeck.ecos.data.sql.ecostype.DbEcosModelService.mapAttToColumn (ecos-data) and its
    // deploy-time guard ru.citeck.ecos.model.service.validation.ModelAttColumnNameValidator (ecos-model):
    // only names matching this pattern and not starting with '_' become database columns. The rule is
    // applied to the column name, because for an aspect ecos-data sees the attribute id already prefixed.
    private val STORED_ID = Regex("[\\w\\-:]+")

    // A computed attribute is stored only when it has a storing type; otherwise its value is
    // recalculated on each read and no column is created for it. Both enums default to NONE.
    private const val COMPUTED = "computed"
    private const val COMPUTED_TYPE = "type"
    private const val COMPUTED_STORING_TYPE = "storingType"
    private const val NONE = "NONE"

    fun collect(file: PsiFile, typeId: String): List<ModelAttColumn> {
        return when (typeId) {
            ArtifactTypes.TYPE_ASPECT -> collectAspect(file)
            ArtifactTypes.TYPE_TYPE -> collectType(file)
            else -> emptyList()
        }
    }

    private fun collectAspect(file: PsiFile): List<ModelAttColumn> {
        val prefix = scalar(file, ASPECT_PREFIX).ifBlank { scalar(file, ASPECT_ID) }
        return columns(file, listOf(ASPECT_ATTS, ASPECT_SYS_ATTS), aspect = true) { "$prefix:$it" }
    }

    // Storage of the type is deliberately not taken into account: ModelAttColumnNameValidator (ecos-model)
    // validates every type definition on deploy, whatever its storageType, so a name rejected there must
    // be reported here too.
    private fun collectType(file: PsiFile): List<ModelAttColumn> {
        return columns(file, listOf(TYPE_ATTS, TYPE_SYS_ATTS), aspect = false) { it }
    }

    private fun columns(
        file: PsiFile,
        paths: List<PsiJPath>,
        aspect: Boolean,
        columnName: (String) -> String
    ): List<ModelAttColumn> {
        return paths.flatMap { it.getStrListWithElements(file) }.mapNotNull { (_, element) ->
            val attId = scalarValue(element)
            if (attId.isBlank()) {
                return@mapNotNull null
            }
            val name = columnName(attId)
            if (isStoredAsColumn(name, element)) {
                ModelAttColumn(name, element, aspect)
            } else {
                null
            }
        }
    }

    private fun scalar(file: PsiFile, path: PsiJPath): String {
        val element = path.getStrListWithElements(file).firstOrNull()?.second ?: return ""
        return scalarValue(element)
    }

    private fun scalarValue(element: PsiElement): String {
        return when (element) {
            is YAMLScalar -> element.textValue
            is JsonStringLiteral -> element.value
            else -> JsonPsiUtil.stripQuotes(element.text)
        }
    }

    private fun isStoredAsColumn(columnName: String, idElement: PsiElement): Boolean {
        return !columnName.startsWith("_") && STORED_ID.matches(columnName) && isStoredComputed(idElement)
    }

    private fun isStoredComputed(idElement: PsiElement): Boolean {
        val computed = attributeElement(idElement)?.getByKey(COMPUTED) ?: return true
        return computedValue(computed, COMPUTED_TYPE) == NONE ||
            computedValue(computed, COMPUTED_STORING_TYPE) != NONE
    }

    private fun computedValue(computed: PsiJPathElement, key: String): String {
        val element = computed.getByKey(key)?.getPsiElement() ?: return NONE
        return scalarValue(element).ifBlank { NONE }
    }

    /** Object the attribute id belongs to, to read the fields declared next to the id. */
    private fun attributeElement(idElement: PsiElement): PsiJPathElement? {
        return when (idElement) {
            is JsonValue -> PsiTreeUtil.getParentOfType(idElement, JsonObject::class.java)
                ?.let { PsiJPathJsonElement(it) }
            is YAMLValue -> PsiTreeUtil.getParentOfType(idElement, YAMLMapping::class.java)
                ?.let { PsiJPathYamlElement(it) }
            else -> null
        }
    }
}
