package ru.citeck.idea.references.resolvers

import com.intellij.psi.xml.XmlTag
import ru.citeck.idea.references.PsiElementValueResolver

class XmlTagValueResolver : PsiElementValueResolver<XmlTag> {

    override fun getPsiElementType(): Class<XmlTag> {
        return XmlTag::class.java
    }

    override fun getValue(psiElement: XmlTag): Any {
        return psiElement.value.text
    }
}
