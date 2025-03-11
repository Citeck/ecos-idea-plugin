package ru.citeck.idea.references.resolvers;

import com.intellij.psi.PsiLiteralExpression;
import ru.citeck.idea.references.PsiElementValueResolver;

public class PsiLiteralExpressionValueResolver implements PsiElementValueResolver<PsiLiteralExpression> {

    @Override
    public Class<PsiLiteralExpression> getPsiElementType() {
        return PsiLiteralExpression.class;
    }

    @Override
    public Object getValue(PsiLiteralExpression psiElement) {
        return psiElement.getValue();
    }
}
