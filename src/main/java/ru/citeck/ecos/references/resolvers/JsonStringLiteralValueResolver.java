package ru.citeck.ecos.references.resolvers;

import com.intellij.json.psi.JsonStringLiteral;
import ru.citeck.ecos.references.PsiElementValueResolver;

public class JsonStringLiteralValueResolver implements PsiElementValueResolver<JsonStringLiteral> {

    @Override
    public Class<JsonStringLiteral> getPsiElementType() {
        return JsonStringLiteral.class;
    }

    @Override
    public Object getValue(JsonStringLiteral psiElement) {
        return psiElement.getValue();
    }
}
