package ru.citeck.idea.artifacts.type

import com.intellij.json.psi.JsonObject
import com.intellij.json.psi.JsonProperty
import com.intellij.json.psi.JsonPsiUtil
import com.intellij.json.psi.JsonValue
import com.intellij.openapi.util.Pair
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.PsiFileImpl
import org.apache.commons.lang3.StringUtils
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.YAMLFile
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.ecos.commons.json.Json
import ru.citeck.ecos.commons.json.YamlUtils
import ru.citeck.idea.utils.CiteckPsiUtils
import java.nio.charset.StandardCharsets
import java.util.*

class JsonYamlArtifactType : ArtifactTypeController {

    companion object {
        private const val CONTENT_ATT_ALIAS = "content"
    }

    override fun getFetchAtts(file: PsiFile): Map<String, String> {
        return mapOf(CONTENT_ATT_ALIAS to "?json")
    }

    override fun prepareDeployAtts(file: PsiFile): ObjectData {
        val atts = ObjectData.create()
        val dataStr = StringBuilder("data:")
        if (file is YAMLFile) {
            dataStr.append("application/x-yaml")
        } else {
            dataStr.append("application/json")
        }
        dataStr.append(";base64,")
        dataStr.append(
            Base64.getEncoder().encodeToString(file.text.toByteArray(StandardCharsets.UTF_8))
        )
        atts["_self"] = DataValue.createObj().set("url", dataStr.toString())
        return atts
    }

    override fun getArtifactId(file: PsiFile): String {
        val result = if (file is YAMLFile) {
            getArtifactIdFromYaml(file)
        } else {
            getArtifactIdFromJson(file)
        }
        return JsonPsiUtil.stripQuotes(StringUtils.defaultIfBlank(result, "")!!)
    }

    override fun writeFetchedData(file: PsiFile, value: ObjectData) {
        var newData = value[CONTENT_ATT_ALIAS]
        if (newData.isEmpty()) {
            error(
                "Content of fetched artifact is empty. " +
                "The project artifact will not be updated."
            )
        }

        val newIdValue = newData["id"]
        if (newIdValue.isEmpty()) {
            error(
                "'id' value is empty in the fetched data. " +
                "It seems that the artifact does not exist on the server. " +
                "The project artifact will not be updated."
            )
        }

        val currentRawContent = file.text
        val currentData = if (file is YAMLFile) {
            DataValue.of(YamlUtils.read(currentRawContent))
        } else {
            Json.mapper.readDataNotNull(currentRawContent)
        }
        val currentIdValue = currentData["id"]
        if (currentIdValue != newIdValue) {
            error(
                "Fetched id $newIdValue is not match " +
                "with current artifact id $currentIdValue. " +
                "The project artifact will not be updated."
            )
        }

        newData = FetchedJsonYamlFormatter.sortProps(currentData, newData)

        val formattedContent = if (file is YAMLFile) {
            val newDataRawContent = YamlUtils.toString(newData)
            FetchedJsonYamlFormatter.restoreYamlFormat(currentRawContent, newDataRawContent)
        } else {
            Json.mapper.toPrettyStringNotNull(newData)
        }
        CiteckPsiUtils.setContent(file, formattedContent)
    }

    private fun getArtifactIdFromJson(file: PsiFile): String? {
        if (file !is PsiFileImpl) {
            return ""
        }
        val jsonObject = file.findChildByClass(
            JsonObject::class.java
        )
        if (jsonObject == null) {
            return null
        }
        val idProperty = jsonObject.findProperty("id")
        return Optional.ofNullable(idProperty)
            .map { obj: JsonProperty -> obj.value }
            .map { obj: JsonValue? -> obj!!.text }
            .orElse("")
    }

    private fun getArtifactIdFromYaml(file: YAMLFile): String {
        return Optional
            .ofNullable(YAMLUtil.getValue(file, "id"))
            .map { it: Pair<PsiElement, String?>? -> it!!.getFirst().text }
            .orElse("")
    }
}
