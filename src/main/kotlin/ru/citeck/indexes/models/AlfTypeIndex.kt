package ru.citeck.indexes.models

import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import com.intellij.refactoring.suggested.startOffset
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import org.jetbrains.annotations.NotNull
import ru.citeck.alfresco.Association
import ru.citeck.alfresco.Property
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
        if (root.namespace != "http://www.alfresco.org/model/dictionary/1.0") return result

        root.findFirstSubTag("types")?.findSubTags("type")?.forEach { xType ->
            val name = xType.getAttributeValue("name") ?: return@forEach
            val nameParts = name.split(":")

            val prefix = if (nameParts.size > 1) nameParts[0] else ""
            val parent = xType.findFirstSubTag("parent")?.value?.text

            val properties = loadProperties(xType)
            val associations = loadAssociations(xType)

            val type = Type(name, prefix, parent, properties, associations, xType.startOffset)

            result[name] = type
        }
        return result
    }

    private fun loadAssociations(xType: XmlTag): MutableList<Association> {
        val associations = mutableListOf<Association>()
        xType.findFirstSubTag("associations")?.findSubTags("association")?.forEach { xAssoc ->
            val assocName = xAssoc.getAttributeValue("name") ?: ""
            val target = xAssoc.findFirstSubTag("target")?.findFirstSubTag("class")?.value?.text ?: ""
            val association = Association(assocName, target)
            associations.add(association)
        }
        return associations
    }

    private fun loadProperties(xType: @NotNull XmlTag): MutableList<Property> {
        val properties = mutableListOf<Property>()
        xType.findFirstSubTag("properties")?.findSubTags("property")?.forEach { xProperty ->
            val propertyName = xProperty.getAttributeValue("name") ?: ""
            val propertyType = xProperty.findFirstSubTag("type")?.value?.text ?: ""
            val property = Property(propertyName, propertyType)
            properties.add(property)
        }
        return properties
    }

}