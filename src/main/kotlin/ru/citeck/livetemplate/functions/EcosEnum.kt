package ru.citeck.livetemplate.functions

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.template.Expression
import com.intellij.codeInsight.template.ExpressionContext
import com.intellij.codeInsight.template.macro.EnumMacro

class EcosEnum : EnumMacro() {

    override fun getName(): String {
        return "ecosEnum"
    }

    override fun getPresentableName(): String {
        return "ecosEnum()"
    }

    override fun calculateLookupItems(params: Array<Expression>, context: ExpressionContext?): Array<LookupElement>? {
        if (params.isEmpty()) return null
        val res = params[0].calculateResult(context) ?: return null
        EcosEnumVariant.values().forEach { variant ->
            if (variant.type == res.toString()) return variant.lookupElements
        }
        return null
    }

}