package ru.citeck.livetemplates.functions

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.macro.EnumMacro
import ru.citeck.metadata.providers.ModelsProvider

class EcosTypesEnumMacro : EnumMacro() {

    override fun getName(): String {
        return "ecosTypes"
    }

    override fun getPresentableName(): String {
        return "ecosTypes()"
    }

    override fun calculateLookupItems(params: Array<Expression>, context: ExpressionContext?): Array<LookupElement>? {
        val project = context?.project ?: return null
        val lookupElements = ArrayList<LookupElement>()
        val models = project.getService(ModelsProvider::class.java).data
        models?.forEach { model ->
            model.types?.forEach { type ->
                lookupElements.add(LookupElementBuilder.create(type.name))
            }
        }
        lookupElements.sortBy { it.lookupString }
        return lookupElements.toTypedArray()
    }

}