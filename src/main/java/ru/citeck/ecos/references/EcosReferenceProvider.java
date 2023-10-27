package ru.citeck.ecos.references;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.index.IndexKey;

@RequiredArgsConstructor
public class EcosReferenceProvider extends PsiReferenceProvider {

    public static final ExtensionPointName<PsiElementValueResolver<?>> EP_NAME =
        ExtensionPointName.create("ru.citeck.ecos.psiElementValueResolver");

    @Override
    public @NotNull PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
        String value = (String) EP_NAME
            .extensions()
            .map(psiElementValueResolver -> psiElementValueResolver.getPsiElementValue(element))
            .filter(val -> val instanceof String)
            .findFirst()
            .orElse(null);

        if (value == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        return ServiceRegistry
            .getIndexesService(element.getProject())
            .stream(new IndexKey(IndexKey.REFERENCE_TYPE, value))
            .map(indexValue -> new EcosReference(element, indexValue))
            .toArray(PsiReference[]::new);

    }

    public static class Contributor extends PsiReferenceContributor {
        @Override
        public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
            EcosReferenceProvider provider = new EcosReferenceProvider();
            EcosReferenceProvider
                .EP_NAME
                .extensions()
                .forEach(psiElementValueResolver -> registrar.registerReferenceProvider(
                    PlatformPatterns.psiElement(psiElementValueResolver.getPsiElementType()),
                    provider
                ));
        }
    }

}
