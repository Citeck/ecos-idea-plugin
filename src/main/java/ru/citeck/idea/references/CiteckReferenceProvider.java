package ru.citeck.idea.references;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import ru.citeck.idea.index.IndexKey;
import ru.citeck.idea.index.IndexesService;

@RequiredArgsConstructor
public class CiteckReferenceProvider extends PsiReferenceProvider {

    @Override
    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        String value = (String) PsiElementValueResolver.EP_NAME
            .getExtensionsIfPointIsRegistered()
            .stream()
            .map(psiElementValueResolver -> psiElementValueResolver.getPsiElementValue(element))
            .filter(val -> val instanceof String)
            .findFirst()
            .orElse(null);

        if (value == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return IndexesService.getInstance(element.getProject())
            .stream(new IndexKey(IndexKey.REFERENCE_TYPE, value))
            .map(indexValue -> new EcosReference(element, indexValue))
            .toArray(PsiReference[]::new);

    }

    public static class Contributor extends PsiReferenceContributor {
        @Override
        public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
            CiteckReferenceProvider provider = new CiteckReferenceProvider();
            PsiElementValueResolver.EP_NAME
                .getExtensionsIfPointIsRegistered()
                .forEach(psiElementValueResolver -> registrar.registerReferenceProvider(
                    PlatformPatterns.psiElement(psiElementValueResolver.getPsiElementType()),
                    provider
                ));
        }
    }

}
