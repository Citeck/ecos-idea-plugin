package ru.citeck.ecos.completion.java.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.JavaCompletionUtil;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.completion.java.JavaCompletionProvider;
import ru.citeck.ecos.index.indexers.qname.QName;

import java.util.Set;

public class QNamesCompletionProvider implements JavaCompletionProvider {

    @Override
    public void addCompletions(
            @NotNull CompletionParameters parameters,
            @NotNull ProcessingContext context,
            @NotNull CompletionResultSet result
    ) {
        Project project = parameters.getPosition().getProject();
        PsiClassType qNamePattern = PsiType.getTypeByName(QName.QNAME_PATTERN, project, GlobalSearchScope.allScope(project));
        Set<PsiType> expectedTypes = JavaCompletionUtil.getExpectedTypes(parameters);
        if (expectedTypes == null || expectedTypes.stream().noneMatch(qNamePattern::isAssignableFrom)) {
            return;
        }
        result.addAllElements(
                ServiceRegistry.getQNameService(project).createJavaLookupElements()
        );
    }

    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement();
    }

}
