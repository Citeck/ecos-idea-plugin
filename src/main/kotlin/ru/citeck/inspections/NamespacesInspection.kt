package ru.citeck.inspections

import com.intellij.codeInspection.*
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.source.xml.XmlFileImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.*
import com.intellij.util.containers.toArray
import com.intellij.util.indexing.FileBasedIndex
import ru.citeck.alfresco.Namespace
import ru.citeck.indexes.models.AbstractAlfrescoIndex
import ru.citeck.indexes.models.AlfNamespaceIndex

class NamespacesInspection : XmlSuppressableInspectionTool() {

    private class NamespaceQuickFix(val namespace: Namespace)  : LocalQuickFix {

        override fun getFamilyName(): String {
            return "Import namespace \"${namespace.uri}\""
        }

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            (descriptor.psiElement.containingFile as XmlFileImpl).rootTag?.findFirstSubTag("imports")?.addSubTag(PsiTreeUtil.findChildOfType(
                PsiFileFactory.getInstance(project).createFileFromText(Language.findLanguageByID("XML")!!, "<import uri=\"${namespace.uri}\" prefix=\"${namespace.prefix}\"/>"), XmlTag::class.java), false)
        }

    }

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor?>? {

        val fixes = mutableMapOf<String, NamespaceQuickFix?>()

        fun createProblemDescriptor(psiElement: PsiElement, prefix: String): ProblemDescriptor {
            if (!fixes.containsKey(prefix)) {
                val namespace = FileBasedIndex.getInstance().getValues(AlfNamespaceIndex.NAME, prefix, GlobalSearchScope.allScope(file.project)).firstOrNull()
                fixes[prefix] = if (namespace != null) NamespaceQuickFix(namespace) else null
            }
            return manager.createProblemDescriptor(psiElement, "Namespace prefix not imported", fixes[prefix], ProblemHighlightType.ERROR, true)
        }

        file as XmlFile
        val rootTag = file.rootTag ?: return null
        if (rootTag.namespace != AbstractAlfrescoIndex.MODEL_NAMESPACE) return null

        val prefixes = mutableSetOf<String>()
        rootTag.findFirstSubTag("namespaces")?.findSubTags("namespace")?.forEach { namespace ->
            namespace.getAttributeValue("prefix")?.let { prefix -> prefixes.add(prefix) }
        }
        rootTag.findFirstSubTag("imports")?.findSubTags("import")?.forEach { import ->
            import.getAttributeValue("prefix")?.let { prefix -> prefixes.add(prefix) }
        }

        val result = mutableListOf<ProblemDescriptor>()

        val tags = PsiTreeUtil.findChildrenOfType(rootTag, XmlTag::class.java)

        tags.filter { (it.name == "parent" || it.name == "type" || it.name == "class" || it.name == "aspect") && it.subTags.isEmpty() }.forEach {
            val name = it.value.text.split(":")
            if (name.size != 2) return@forEach

            if (!prefixes.contains(name[0])) {
                val textElement = PsiTreeUtil.findChildOfType(it, XmlText::class.java) ?: return@forEach
                result.add(createProblemDescriptor(textElement, name[0]))
            }

        }

        tags.filter { (it.name == "type" || it.name == "property" || it.name == "association" || it.name == "constraint" || it.name == "aspect") && it.getAttributeValue("name") != null }.forEach { xmlTag ->
            val name = xmlTag.getAttributeValue("name")!!.split(":")
            if (name.size != 2) return@forEach
            if (!prefixes.contains(name[0])) {
                val attribute = PsiTreeUtil.findChildrenOfType(xmlTag, XmlAttribute::class.java).first { it.name == "name" }
                val textElement = PsiTreeUtil.findChildOfType(attribute, XmlAttributeValue::class.java) ?: return@forEach
                result.add(createProblemDescriptor(textElement, name[0]))
            }
        }

        return result.toArray(arrayOf())

    }

    override fun processFile(file: PsiFile, manager: InspectionManager): MutableList<ProblemDescriptor> {
        println(file)
        return mutableListOf()
    }
}