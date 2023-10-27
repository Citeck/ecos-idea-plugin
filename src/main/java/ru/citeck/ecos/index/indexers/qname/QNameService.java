package ru.citeck.ecos.index.indexers.qname;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.gist.GistManager;
import com.intellij.util.gist.PsiFileGist;
import icons.Icons;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.index.indexers.AlfrescoModelIndexer;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;

import java.util.*;
import java.util.stream.Collectors;

public class QNameService {

    private final Project project;

    private final PsiFileGist<List<QName>> qnamesGist;

    public QNameService(Project project) {
        this.project = project;
        qnamesGist = GistManager.getInstance().newPsiFileGist(
            "ru.citeck.plugin.ecos.indexes.qnames.QName-" + project.getLocationHash(), 1, new QNameListExternalizer(), this::findQNames
        );
    }

    private List<QName> findQNames(PsiFile psiFile) {

        List<QName> result = new ArrayList<>();

        Map<String, String> prefixesMap = new HashMap<>();

        PsiTreeUtil.findChildrenOfType(psiFile, PsiField.class)
            .stream()
            .filter(t -> t.getText().contains("QName.createQName"))
            .forEach(psiField -> {
                PsiMethodCallExpression psiMethodCallExpression = PsiTreeUtil.findChildOfType(psiField, PsiMethodCallExpression.class);
                if (psiMethodCallExpression == null) {
                    return;
                }
                PsiReferenceExpression methodExpression = psiMethodCallExpression.getMethodExpression();
                if (!"QName.createQName".equals(methodExpression.getText())) {
                    return;
                }
                PsiExpression[] argExpressions = psiMethodCallExpression.getArgumentList().getExpressions();
                if (argExpressions == null || argExpressions.length != 2) {
                    return;
                }

                PsiModifierList psiModifierList = PsiTreeUtil.findChildOfType(psiField, PsiModifierList.class);
                if (psiModifierList == null) {
                    return;
                }

                if (!(psiModifierList.hasModifierProperty(PsiModifier.PUBLIC) &&
                    psiModifierList.hasModifierProperty(PsiModifier.STATIC) &&
                    psiModifierList.hasModifierProperty(PsiModifier.FINAL))) {
                    return;
                }

                String uri = resolveValue(argExpressions[0]);
                if (uri == null) {
                    return;
                }
                String localName = resolveValue(argExpressions[1]);
                if (localName == null) {
                    return;
                }

                if (!prefixesMap.containsKey(uri)) {
                    List<IndexValue> namespaces = ServiceRegistry
                        .getIndexesService(project)
                        .list(new IndexKey(AlfrescoModelIndexer.NAMESPACE, uri));

                    if (namespaces.size() != 0) {
                        prefixesMap.put(uri, namespaces.get(0).getProperty("prefix"));
                    } else {
                        prefixesMap.put(uri, null);
                    }
                }
                String prefix = prefixesMap.get(uri);
                if (prefix == null) {
                    return;
                }

                String javaField = psiField.getName();
                String javaClass = Optional
                    .ofNullable(PsiTreeUtil.getParentOfType(psiField, PsiClass.class))
                    .map(PsiNamedElement::getName)
                    .orElse(null);

                result.add(new QName(localName, uri, prefix, javaField, javaClass));
            });

        return result;
    }

    public List<QName> getQNames() {
        return ServiceRegistry
            .getIndexesService(project)
            .stream(AlfrescoQNameIndexer.CONTAINS_QNAMES_KEY)
            .map(IndexValue::getFile)
            .map(EcosVirtualFileUtils::getFileByPath)
            .filter(Objects::nonNull)
            .map(file -> qnamesGist.getFileData(PsiUtil.getPsiFile(project, file)))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    public List<LookupElement> createJavaLookupElements() {
        return getQNames()
            .stream()
            .map(qName -> LookupElementBuilder
                .create(qName.getPrefix() + "_" + qName.getLocalName())
                .withIcon(Icons.AlfrescoLogo)
                .withTypeText(qName.getJavaClass() + "." + qName.getJavaField())
                .withInsertHandler((insertionContext, item) -> ApplicationManager.getApplication().runWriteAction(() ->
                    CommandProcessor.getInstance().runUndoTransparentAction(() ->
                        insertionContext.getDocument().replaceString(
                            insertionContext.getStartOffset(),
                            insertionContext.getTailOffset(),
                            qName.getJavaClass() + "." + qName.getJavaField()
                        )))
                ))
            .collect(Collectors.toList());
    }

    private String resolveValue(PsiExpression psiExpression) {
        if (psiExpression instanceof PsiLiteralExpression) {
            Object value = ((PsiLiteralExpression) psiExpression).getValue();
            if (value != null) {
                return value.toString();
            }
            return "";
        } else if (psiExpression instanceof PsiReferenceExpression) {
            return Optional
                .of((PsiReferenceExpression) psiExpression)
                .map(PsiReference::resolve)
                .filter(PsiField.class::isInstance)
                .map(PsiField.class::cast)
                .map(PsiVariable::computeConstantValue)
                .map(Object::toString)
                .orElse(null);
        } else {
            return null;
        }
    }

}
