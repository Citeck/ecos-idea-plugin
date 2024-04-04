package ru.citeck.ecos.references.resolvers;

import org.jetbrains.yaml.psi.YAMLScalar;
import ru.citeck.ecos.references.PsiElementValueResolver;

public class YAMLScalarValueResolver implements PsiElementValueResolver<YAMLScalar> {

    @Override
    public Class<YAMLScalar> getPsiElementType() {
        return YAMLScalar.class;
    }

    @Override
    public Object getValue(YAMLScalar psiElement) {
        return psiElement.getText();
    }

}
