package ru.citeck.idea.artifacts.type

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlFile
import ru.citeck.ecos.commons.data.DataValue
import ru.citeck.ecos.commons.data.ObjectData
import ru.citeck.ecos.webapp.api.entity.EntityRef
import ru.citeck.idea.utils.CiteckPsiUtils
import java.util.*
import kotlin.collections.Map
import kotlin.collections.mapOf

class ProcXmlArtifactType(
    private val subType: SubType
) : ArtifactTypeController {

    override fun getArtifactUrl(ref: EntityRef): String {
        return when (subType) {
            SubType.DMN -> "/v2/dmn-editor?recordRef=$ref&ws=admin\$workspace"
            SubType.BPMN -> "/v2/bpmn-editor?recordRef=$ref&ws=admin\$workspace"
        }
    }

    override fun prepareDeployAtts(file: PsiFile): ObjectData {
        val vFile = file.virtualFile
        return ObjectData.create().set("_content", DataValue.createArr().add(
            DataValue.createObj()
            .set("storage", "base64")
            .set("type", "text/xml")
            .set("name", vFile.name)
            .set("originalName", vFile.name)
            .set("url", "data:text/xml;base64," + Base64.getEncoder().encodeToString(file.text.toByteArray()))
        ))
    }

    override fun getArtifactIdPsiElement(file: PsiFile): PsiElement? {
        if (file !is XmlFile) {
            return null
        }
        val (attId, attNs) = when (subType) {
            SubType.BPMN -> "processDefId" to "http://www.citeck.ru/ecos/bpmn/1.0"
            SubType.DMN -> "defId" to "http://www.citeck.ru/ecos/dmn/1.0"
        }
        return file.rootTag
            ?.getAttribute(attId, attNs)
            ?.valueElement
    }

    override fun getFetchAtts(file: PsiFile): Map<String, String> {
        return mapOf("content" to "data?str")
    }

    override fun writeFetchedData(file: PsiFile, value: ObjectData) {
        val contentBase64 = value["content"].asText()

        val decodedContent = String(Base64.getDecoder().decode(contentBase64), Charsets.UTF_8)
            .replace("\r\n", "\n")

        CiteckPsiUtils.setContent(file, decodedContent);
    }

    override fun isIndexable(file: PsiFile): Boolean {
        return true
    }

    enum class SubType {
        DMN, BPMN
    }
}
