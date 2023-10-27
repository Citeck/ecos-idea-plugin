package ru.citeck.ecos.references.resolvers;

import com.intellij.psi.xml.XmlAttributeValue;
import ru.citeck.ecos.references.PsiElementValueResolver;

public class XmlAttributeValueValueResolver implements PsiElementValueResolver<XmlAttributeValue> {

    @Override
    public Class<XmlAttributeValue> getPsiElementType() {
        return XmlAttributeValue.class;
    }

    @Override
    public Object getValue(XmlAttributeValue psiElement) {
        return psiElement.getValue();
    }
}
