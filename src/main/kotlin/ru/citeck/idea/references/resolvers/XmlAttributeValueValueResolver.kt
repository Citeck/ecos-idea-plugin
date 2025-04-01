package ru.citeck.idea.references.resolvers

import com.intellij.psi.xml.XmlAttributeValue
import ru.citeck.idea.references.PsiElementValueResolver

class XmlAttributeValueValueResolver : PsiElementValueResolver<XmlAttributeValue> {

    override fun getPsiElementType(): Class<XmlAttributeValue> {
        return XmlAttributeValue::class.java
    }

    override fun getValue(psiElement: XmlAttributeValue): Any {
        return psiElement.value
    }
}
