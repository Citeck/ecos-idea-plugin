package ru.citeck.metadata.providers

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.ProjectScope
import com.intellij.psi.xml.XmlFile
import ru.citeck.metadata.MetadataProvider
import ru.citeck.metadata.alfresco.Model

class ModelsProvider(project: Project) : MetadataProvider<List<Model>>(project) {

    override fun loadData(): List<Model> {

        val models = mutableListOf<Model>()

        val xmlMapper = XmlMapper()
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        ApplicationManager.getApplication().runReadAction {
            FilenameIndex.getAllFilesByExt(
                project,
                "xml",
                GlobalSearchScope.projectScope(project).uniteWith(ProjectScope.getLibrariesScope(project))
            )
                .filter {
                    it.parent.path.endsWith("/model") && (it.url.startsWith("jar://") && it.path.contains("-sources.jar!") || it.url.startsWith("file://"))
                }
                .forEach { vFile ->
                    val psiFile = PsiManager.getInstance(project).findFile(vFile) as XmlFile

                    val model = xmlMapper.readValue(psiFile.text, Model::class.java)

                    models.add(model)
                }
        }
        return models
    }
}