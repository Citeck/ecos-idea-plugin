package ru.citeck.idea.references.resolvers

import com.intellij.psi.PsiLiteralExpression
import ru.citeck.idea.references.PsiElementValueResolver

class PsiLiteralExpressionValueResolver : PsiElementValueResolver<PsiLiteralExpression> {

    override fun getPsiElementType(): Class<PsiLiteralExpression> {
        return PsiLiteralExpression::class.java
    }

    override fun getValue(psiElement: PsiLiteralExpression): Any? {
        return psiElement.value
    }
}
