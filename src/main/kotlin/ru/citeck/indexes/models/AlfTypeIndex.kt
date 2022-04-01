package ru.citeck.indexes.models

import com.intellij.psi.xml.XmlFile
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import ru.citeck.alfresco.Type

class AlfTypeIndex : AbstractAlfrescoIndex<Type>(Type::class.java) {

    companion object {
        val NAME = ID.create<String, Type>("ru.citeck.indexes.models.AlfTypeIndex")
    }

    override fun getName(): ID<String, Type> {
        return NAME
    }

    override fun map(inputData: FileContent): MutableMap<String, Type> {
        val result = mutableMapOf<String, Type>()
        val root = (inputData.psiFile as XmlFile).document?.rootTag ?: return result
        if (root.namespace != MODEL_NAMESPACE) return result

        root.findFirstSubTag("types")?.findSubTags("type")?.forEach { xType ->
            val name = xType.getAttributeValue("name") ?: return@forEach
            val nameParts = name.split(":")

            val prefix = if (nameParts.size > 1) nameParts[0] else ""
            val parent = xType.findFirstSubTag("parent")?.value?.text
            val type = Type(name, prefix, parent, xType.startOffset)

            result[name] = type
        }
        return result
    }


}