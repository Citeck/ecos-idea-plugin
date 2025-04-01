package ru.citeck.idea.references.resolvers

import com.intellij.lang.javascript.psi.JSLiteralExpression
import ru.citeck.idea.references.PsiElementValueResolver

class JSLiteralExpressionValueResolver : PsiElementValueResolver<JSLiteralExpression> {

    override fun getPsiElementType(): Class<JSLiteralExpression> {
        return JSLiteralExpression::class.java
    }

    override fun getValue(psiElement: JSLiteralExpression): Any? {
        return psiElement.stringValue
    }
}
