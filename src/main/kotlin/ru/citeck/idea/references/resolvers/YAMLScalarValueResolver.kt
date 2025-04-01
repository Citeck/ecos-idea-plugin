package ru.citeck.idea.references.resolvers

import org.jetbrains.yaml.psi.YAMLScalar
import ru.citeck.idea.references.PsiElementValueResolver

class YAMLScalarValueResolver : PsiElementValueResolver<YAMLScalar> {

    override fun getPsiElementType(): Class<YAMLScalar> {
        return YAMLScalar::class.java
    }

    override fun getValue(psiElement: YAMLScalar): Any? {
        return psiElement.text
    }
}
