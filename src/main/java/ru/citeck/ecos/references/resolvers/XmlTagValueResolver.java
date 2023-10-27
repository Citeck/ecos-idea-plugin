package ru.citeck.ecos.references.resolvers;

import com.intellij.psi.xml.XmlTag;
import ru.citeck.ecos.references.PsiElementValueResolver;

public class XmlTagValueResolver implements PsiElementValueResolver<XmlTag> {

    @Override
    public Class<XmlTag> getPsiElementType() {
        return XmlTag.class;
    }

    @Override
    public Object getValue(XmlTag psiElement) {
        return psiElement.getValue().getText();
    }

}
