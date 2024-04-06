package ru.citeck.ecos.completion.js.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.psi.JSIndexedPropertyAccessExpression;
import com.intellij.openapi.util.text.Strings;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.completion.js.JsCompletionProvider;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;

import java.util.Map;
import java.util.Optional;

public class NodeAttributesShortQnamesCompletionProvider implements JsCompletionProvider {

    private final static Map<String, String> NODE_ATTRIBUTES_MAPPING = Map.of(
            "properties", AlfrescoModelIndexer.PROPERTY,
            "assocs", AlfrescoModelIndexer.ASSOCIATION,
            "associations", AlfrescoModelIndexer.ASSOCIATION,
            "childAssocs", AlfrescoModelIndexer.CHILD_ASSOCIATION,
            "childAssociations", AlfrescoModelIndexer.CHILD_ASSOCIATION
    );

    @Override
    public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

        if (getInjectedInFile(parameters) != null) {
            return;
        }

        PsiElement psiElement = parameters.getPosition();

        String quailifier = Optional
                .ofNullable(PsiTreeUtil.getParentOfType(psiElement, JSIndexedPropertyAccessExpression.class))
                .map(JSIndexedPropertyAccessExpression::getQualifier)
                .map(PsiElement::getText)
                .orElse(null);

        if (Strings.isEmpty(quailifier)) {
            return;
        }

        String indexType = NODE_ATTRIBUTES_MAPPING
                .entrySet()
                .stream()
                .filter(entry -> quailifier.endsWith("." + entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        if (indexType == null) {
            return;
        }

        ServiceRegistry
                .getIndexesService(psiElement.getProject())
                .stream(new IndexKey(indexType))
                .forEach(indexValue -> result.addElement(
                        LookupElementBuilder
                                .create(indexValue.getId())
                                .withIcon(indexValue.getIcon())
                ));

    }

    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement().withParent(
                PlatformPatterns.psiElement(JSElementTypes.STRING_TEMPLATE_EXPRESSION)
                        .withParent(
                                PlatformPatterns.psiElement(JSElementTypes.INDEXED_PROPERTY_ACCESS_EXPRESSION)
                        )
        );
    }
}
