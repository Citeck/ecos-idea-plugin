package ru.citeck.idea.references.resolvers;

import org.jetbrains.yaml.psi.YAMLScalar;
import ru.citeck.idea.references.PsiElementValueResolver;

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
