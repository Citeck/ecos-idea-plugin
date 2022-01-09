package ru.citeck.metadata.providers

import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.parentOfType
import ru.citeck.metadata.MetadataProvider
import ru.citeck.metadata.QName

class QNamesProvider(project: Project) : MetadataProvider<List<QName>>(project) {

    override fun loadData(): List<QName>? {

        val models = project.getService(ModelsProvider::class.java).getData() ?: return null
        val namespaces = HashMap<String, String>()
        models.forEach { model ->
            model.namespaces?.forEach { namespace ->
                namespaces[namespace.uri] = namespace.prefix
            }
        }

        val qNamePsiClass = JavaPsiFacade.getInstance(project)
            .findClass(QName.CLASS, ProjectScope.getLibrariesScope(project))
            ?: return listOf()

        val qnames = mutableListOf<QName>()
        ApplicationManager.getApplication().runReadAction {
            ReferencesSearch.search(qNamePsiClass, GlobalSearchScope.allScope(project))
                .filter { it is PsiJavaCodeReferenceElement && it.parentOfType<PsiField>() != null }
                .map { (it as PsiJavaCodeReferenceElement).parentOfType<PsiField>() }
                .distinct()
                .filter {
                    it!!.hasModifier(JvmModifier.PUBLIC)
                            && it.hasModifier(JvmModifier.STATIC)
                            && it.hasModifier(JvmModifier.FINAL)
                            && it.type.canonicalText == QName.CLASS
                }.forEach { field ->
                    val methodExpr = field?.children?.filterIsInstance<PsiMethodCallExpression>()
                        ?.firstOrNull { it.methodExpression.qualifiedName == "QName.createQName" }
                        ?: return@forEach

                    val argExpressions = methodExpr.argumentList.expressions
                    if (argExpressions.size != 2) return@forEach

                    val uri = resolveValue(argExpressions[0]) ?: return@forEach
                    val localName = resolveValue(argExpressions[1]) ?: return@forEach
                    val prefix = namespaces[uri] ?: return@forEach

                    val pkg = field.parentOfType<PsiJavaFile>()!!.packageName
                    val fieldName = field.name
                    val className = field.parentOfType<PsiClass>()?.name ?: return@forEach
                    qnames.add(QName(localName as String, prefix, fieldName, className))

                }

        }
        qnames.sortBy { it.toString() }
        return qnames
    }


    private fun resolveValue(psiExpression: PsiExpression): Any? {
        if (psiExpression is PsiLiteralExpression) {
            return psiExpression.value
        } else if (psiExpression is PsiReferenceExpression) {
            val target = psiExpression.resolve() ?: return null
            if (target is PsiField) {
                return target.computeConstantValue()
            }
            return null
        } else {
            return null
        }
    }


}