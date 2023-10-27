package ru.citeck.ecos.references.resolvers;

import com.intellij.lang.javascript.psi.JSLiteralExpression;
import ru.citeck.ecos.references.PsiElementValueResolver;

public class JSLiteralExpressionValueResolver implements PsiElementValueResolver<JSLiteralExpression> {

    @Override
    public Class<JSLiteralExpression> getPsiElementType() {
        return JSLiteralExpression.class;
    }

    @Override
    public Object getValue(JSLiteralExpression psiElement) {
        return psiElement.getStringValue();
    }

}
