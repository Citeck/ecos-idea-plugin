package ru.citeck.ecos.completion.js.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.completion.js.JsCompletionProvider;
import ru.citeck.ecos.index.indexers.XmlBeanDefinitionIndexer;

public class BaseProcessorExtensionsCompletionProvider implements JsCompletionProvider {

    @Override
    public void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

        if (getInjectedInFile(parameters) != null) {
            return;
        }

        PsiElement psiElement = parameters.getPosition();
        JSReferenceExpression referenceExpression = PsiTreeUtil.getParentOfType(psiElement, JSReferenceExpression.class);
        if (referenceExpression == null) {
            return;
        }


        ServiceRegistry
            .getIndexesService(referenceExpression.getProject())
            .stream(XmlBeanDefinitionIndexer.JAVASCRIPT_EXTENSION_KEY)
            .forEach(indexValue -> result.addElement(
                LookupElementBuilder
                    .create(indexValue.getId())
                    .withTypeText(indexValue.getProperty("class"))
                    .withIcon(indexValue.getIcon())
            ));

    }

    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement()
            .withParent(PlatformPatterns.psiElement(JSElementTypes.REFERENCE_EXPRESSION))
            .andNot(PlatformPatterns.psiElement().afterLeaf(".", "?."));
    }
}
