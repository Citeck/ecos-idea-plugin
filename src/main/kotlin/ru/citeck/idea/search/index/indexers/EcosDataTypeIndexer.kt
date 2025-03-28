package ru.citeck.idea.search.index.indexers

import com.intellij.json.psi.*
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.util.text.Strings
import com.intellij.util.indexing.FileContent
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLKeyValue
import org.jetbrains.yaml.psi.YAMLSequence
import org.jetbrains.yaml.psi.YAMLSequenceItem
import ru.citeck.idea.files.FileType
import ru.citeck.idea.files.types.citeck.EcosArtifact
import ru.citeck.idea.files.types.citeck.model.DataType
import ru.citeck.idea.search.index.EcosFileIndexer
import ru.citeck.idea.search.index.IndexKey
import ru.citeck.idea.search.index.Indexes.FileIndexes
import ru.citeck.idea.utils.CiteckPsiUtils
import ru.citeck.idea.utils.CommonUtils
import java.util.*
import java.util.stream.Stream

interface EcosDataTypeIndexer : EcosFileIndexer {

    companion object {
        const val ROLE: String = "role"
        const val STATUS: String = "status"
        const val ATTRIBUTE: String = "attribute"

        @JvmStatic
        val MODEL_PARTITIONS_MAPPING = mapOf(
            ROLE to "roles",
            STATUS to "statuses",
            ATTRIBUTE to "attributes"
        )
    }

    class JSON : EcosDataTypeIndexer {

        override fun accept(fileType: FileType): Boolean {
            return fileType is DataType.JSON
        }

        override fun map(inputData: FileContent, indexes: FileIndexes) {

            val psiFile = inputData.psiFile as? JsonFile ?: return

            val typeId = (getFileType(inputData) as EcosArtifact).getFullId(psiFile)
            if (Strings.isEmpty(typeId)) {
                return
            }

            val model = Optional
                .ofNullable(psiFile.topLevelValue)
                .map(CommonUtils.filterAndCast(JsonObject::class.java))
                .map { jsonObject: JsonObject ->
                    jsonObject.findProperty(
                        "model"
                    )
                }
                .map { obj: JsonProperty? -> obj!!.value }
                .map(CommonUtils.filterAndCast(JsonObject::class.java))
                .orElse(null)

            if (model == null) {
                return
            }

            MODEL_PARTITIONS_MAPPING.forEach { (key: String, value: String) ->
                getItems(model, value).forEach { jsonProperty: JsonProperty ->
                    val id = CiteckPsiUtils.getValue(jsonProperty)
                    if (StringUtil.isNotEmpty(id)) {
                        indexes.add(IndexKey(typeId, key), id, jsonProperty)
                    }
                }
            }
        }

        private fun getItems(model: JsonObject, key: String): Stream<JsonProperty> {
            return Optional
                .ofNullable(model.findProperty(key))
                .map { obj: JsonProperty -> obj.value }
                .map(CommonUtils.filterAndCast(JsonArray::class.java))
                .map { obj: JsonArray -> obj.valueList }
                .stream()
                .flatMap { obj: List<JsonValue> -> obj.stream() }
                .map(CommonUtils.filterAndCast(JsonObject::class.java))
                .map { jsonObject: JsonObject -> jsonObject.findProperty("id") }
        }
    }

    class YAML : EcosDataTypeIndexer {
        override fun accept(fileType: FileType): Boolean {
            return fileType is DataType.YAML
        }

        override fun map(inputData: FileContent, indexes: FileIndexes) {
            val psiFile = inputData.psiFile as? YAMLFile ?: return

            val typeId = (getFileType(inputData) as EcosArtifact).getFullId(psiFile)
            if (Strings.isEmpty(typeId)) {
                return
            }

            MODEL_PARTITIONS_MAPPING.forEach { (key: String, value: String) ->
                getItems(psiFile, value).forEach { keyValue: YAMLKeyValue ->
                    val id = keyValue.valueText
                    if (StringUtil.isNotEmpty(id)) {
                        indexes.add(IndexKey(typeId, key), id, keyValue)
                    }
                }
            }
        }

        fun getItems(yamlFile: YAMLFile, key: String): Stream<YAMLKeyValue> {
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
        }
    }
}
