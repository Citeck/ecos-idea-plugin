package ru.citeck.indexes.models

import com.intellij.psi.xml.XmlFile
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import ru.citeck.alfresco.Namespace

class AlfNamespaceIndex : AbstractAlfrescoIndex<Namespace>(Namespace::class.java) {

    companion object {
        val NAME = ID.create<String, Namespace>("ru.citeck.indexes.models.AlfNamespaceIndex")
    }

    override fun getName(): ID<String, Namespace> {
        return NAME
    }

    override fun map(inputData: FileContent): MutableMap<String, Namespace> {

        val result = mutableMapOf<String, Namespace>()
        val root = (inputData.psiFile as XmlFile).document?.rootTag ?: return result
        if (root.namespace != MODEL_NAMESPACE) return result

        val namespaces = root.findFirstSubTag("namespaces")?.findSubTags("namespace") ?: return result
        namespaces.forEach {
            val prefix = it.getAttributeValue("prefix") ?: return@forEach
            val uri = it.getAttributeValue("uri") ?: return@forEach
            val namespace = Namespace(uri, prefix)
            result[uri] = namespace
            result[prefix] = namespace
        }
        return result

    }


}