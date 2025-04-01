package ru.citeck.idea.references.resolvers

import com.intellij.json.psi.JsonStringLiteral
import ru.citeck.idea.references.PsiElementValueResolver

class JsonStringLiteralValueResolver : PsiElementValueResolver<JsonStringLiteral> {

    override fun getPsiElementType(): Class<JsonStringLiteral> {
        return JsonStringLiteral::class.java
    }

    override fun getValue(psiElement: JsonStringLiteral): Any {
        return psiElement.value
    }
}
