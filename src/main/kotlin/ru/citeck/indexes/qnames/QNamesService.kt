package ru.citeck.indexes.qnames

import com.intellij.lang.jvm.JvmModifier
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.parentOfType
import com.intellij.util.gist.GistManager
import com.intellij.util.indexing.FileBasedIndex
import ru.citeck.metadata.QName
import ru.citeck.metadata.providers.ModelsProvider

class QNamesService(val project: Project) {

    private val psiClass: PsiClass? by lazy {
        JavaPsiFacade.getInstance(project)
            .findClass(QName.CLASS, ProjectScope.getLibrariesScope(project))
    }

    private val qNamesGist = GistManager.getInstance()
        .newPsiFileGist("ru.citeck.metadata.QName", 1, QNamesDataExternalizer(), ::findQNames)


    fun getQNamePsiFields(psiFile: PsiFile): List<PsiField> {
        return PsiTreeUtil.findChildrenOfType(psiFile, PsiField::class.java)
            .filter { psiField ->
                psiField?.children?.filterIsInstance<PsiMethodCallExpression>()
                    ?.firstOrNull { it.methodExpression.qualifiedName == "QName.createQName" } != null &&
                        psiField.hasModifier(JvmModifier.PUBLIC) &&
                        psiField.hasModifier(JvmModifier.STATIC) &&
                        psiField.hasModifier(JvmModifier.FINAL)
            }.toList()
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


    private fun findQNames(psiFile: PsiFile): List<QName>? {

        val qnames = mutableListOf<QName>()

        getQNamePsiFields(psiFile)
            .forEach { field ->
                val methodExpr = field?.children?.filterIsInstance<PsiMethodCallExpression>()
                    ?.firstOrNull { it.methodExpression.qualifiedName == "QName.createQName" }
                    ?: return@forEach

                val argExpressions = methodExpr.argumentList.expressions
                if (argExpressions.size != 2) return@forEach

                val localName = resolveValue(argExpressions[1]) ?: return@forEach
                if (localName == "") return@forEach
                val uri = resolveValue(argExpressions[0]) ?: return@forEach

                val fieldName = field.name
                val className = field.parentOfType<PsiClass>()?.name ?: return@forEach
                qnames.add(QName(localName.toString(), uri.toString(), fieldName, className))

            }
        return qnames

    }


    fun findAll(): List<QName> {
        var result = mutableListOf<QName>()
        FileBasedIndex
            .getInstance()
            .getContainingFiles(QNamesFileBasedIndex.NAME, true, GlobalSearchScope.allScope(project))
            .forEach {
                val qnames = qNamesGist.getFileData(PsiUtil.getPsiFile(project, it))
                if (qnames != null) {
                    result.addAll(qnames)
                }
            }
        return result
    }


}