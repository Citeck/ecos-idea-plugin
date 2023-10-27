package ru.citeck.ecos.completion.js.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.lang.javascript.JSElementTypes;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jgoodies.common.base.Strings;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.completion.js.JsCompletionProvider;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.indexers.XmlBeanDefinitionIndexer;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BaseProcessorExtensionMethodsCompletionProvider implements JsCompletionProvider {

    private static final Pattern TEMPLATE_VARIABLE_PATTERN = Pattern.compile("\\$([^$]*)\\$");

    private final InsertHandler<LookupElement> insertHandler = (context, item) -> {

        String templateText = item.getLookupString();

        Project project = context.getProject();
        TemplateManager templateManager = TemplateManager.getInstance(project);

        Template template = templateManager.createTemplate("", "", templateText);

        new Scanner(templateText)
            .findAll(TEMPLATE_VARIABLE_PATTERN)
            .map(MatchResult::group)
            .filter(variable -> !"$END$".equals(variable))
            .map(variable -> variable.substring(1, variable.length() - 1))
            .forEach(variable -> template
                .addVariable(variable, "", "\"" + variable + "\"", true)
            );

        context.getDocument().deleteString(context.getStartOffset(), context.getTailOffset());
        templateManager.startTemplate(context.getEditor(), template);

    };

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

        String qualifier = Optional
            .ofNullable(referenceExpression.getQualifier())
            .map(PsiElement::getText)
            .orElse(null);
        if (Strings.isEmpty(qualifier)) {
            return;
        }

        Project project = referenceExpression.getProject();

        String clazz = ServiceRegistry
            .getIndexesService(project)
            .stream(new IndexKey(XmlBeanDefinitionIndexer.JAVASCRIPT_EXTENSION_KEY_NAME, qualifier))
            .findFirst()
            .map(indexValue -> indexValue.getProperty("class"))
            .orElse("org.alfresco.repo.jscript.ScriptNode");

        PsiClass psiClass = JavaPsiFacade
            .getInstance(project)
            .findClass(clazz, GlobalSearchScope.allScope(project));

        if (psiClass == null) {
            return;
        }

        Arrays
            .stream(psiClass.getAllMethods())
            .filter(psiMethod -> {
                if (psiMethod.isConstructor()) {
                    return false;
                }
                PsiModifierList modifiers = PsiTreeUtil.findChildOfType(psiMethod, PsiModifierList.class);
                if (modifiers == null) {
                    return false;
                }
                return modifiers.hasModifierProperty(PsiModifier.PUBLIC) && !modifiers.hasModifierProperty(PsiModifier.STATIC);
            })
            .forEach(psiMethod -> {

                String methodName = psiMethod.getName();

                PsiType returnType = psiMethod.getReturnType();
                String returnTypeName = Optional
                    .ofNullable(returnType)
                    .map(PsiType::getPresentableText)
                    .orElse("");

                if (returnType != null &&
                    psiMethod.getParameterList().isEmpty() &&
                    methodName.length() >= 4 &&
                    methodName.startsWith("get")
                ) {

                    String propertyName = Introspector.decapitalize(methodName.substring(3));
                    String presentableName = propertyName;
                    if (returnTypeName.startsWith("Map<")) {
                        propertyName += "[$END$]";
                    }

                    result.addElement(
                        buidTemplateElement(
                            propertyName,
                            presentableName,
                            returnTypeName
                        )
                    );
                } else {

                    PsiParameter[] methodParameters = psiMethod.getParameterList().getParameters();

                    String templateMethodParams = Arrays
                        .stream(methodParameters)
                        .map(psiParameter -> "$" + psiParameter.getName() + "$")
                        .collect(Collectors.joining(", "));

                    String presentableMethodParams = Arrays
                        .stream(methodParameters)
                        .map(psiParameter -> psiParameter.getType().getPresentableText() + " " + psiParameter.getName())
                        .collect(Collectors.joining(", "));

                    String template = methodName + "(" + templateMethodParams + ")";
                    if ("void".equals(returnTypeName)) {
                        template += ";";
                    }

                    result.addElement(
                        buidTemplateElement(
                            template,
                            psiMethod.getName() + "(" + presentableMethodParams + ")",
                            returnTypeName
                        ));
                }


            });

    }

    private LookupElement buidTemplateElement(@NotNull String template, @Nullable String presentableText, @Nullable String typeText) {
        LookupElementBuilder lookupElement = LookupElementBuilder.create(template);
        if (presentableText != null) {
            lookupElement = lookupElement.withPresentableText(presentableText);
        }
        if (typeText != null) {
            lookupElement = lookupElement.withTypeText(typeText);
        }

        return lookupElement
            .withIcon(Icons.AlfrescoLogo)
            .withInsertHandler(insertHandler);
    }

    @Override
    public ElementPattern<? extends PsiElement> getElementPattern() {
        return PlatformPatterns.psiElement()
            .withParent(PlatformPatterns.psiElement(JSElementTypes.REFERENCE_EXPRESSION))
            .and(PlatformPatterns.psiElement().afterLeaf(".", "?."));
    }
}
