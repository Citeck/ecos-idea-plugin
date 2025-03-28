package ru.citeck.idea.artifacts

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsActions
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiFileImplUtil
import org.jetbrains.annotations.NonNls
import ru.citeck.idea.project.CiteckProject
import ru.citeck.idea.utils.CiteckVirtualFileUtils
import ru.citeck.idea.utils.EcosMessages
import java.io.File
import java.util.*
import java.util.stream.Stream
import javax.swing.Icon

class CreateArtifactAction(
    text: @NlsActions.ActionText String?,
    description: @NlsActions.ActionDescription String?,
    icon: Icon?,
    private val meta: ArtifactTypeMeta
) : CreateFileFromTemplateAction(text, description, icon), DumbAware {

    override fun getActionName(
        directory: PsiDirectory,
        @NonNls newName: String,
        @NonNls templateName: String
    ): @NlsContexts.Command String {

        return "Create " + meta.name
    }

    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder
    ) {
        builder.setTitle("Artifact ID:")
        builder.addKind(
            meta.type.extension,
            meta.type.icon,
            "custom-template-" + meta.id
        )
    }

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiFile? {

        var targetName = name
        val project = dir.project

        targetName = cleanExtension(targetName)

        val path = getArtifactPath(dir)
        if (path == null) {
            EcosMessages.error(
                "Error",
                "Cannot determine artifact path",
                project
            )
            return null
        }

        var psiDirectory = Optional
            .ofNullable(CiteckVirtualFileUtils.getFileByPath(path))
            .or {
                val newDir = File(path)
                FileUtil.createDirectory(newDir)
                val virtualFile = VirtualFileManager
                    .getInstance()
                    .refreshAndFindFileByNioPath(newDir.toPath())
                Optional.ofNullable(virtualFile)
            }
            .map { _: VirtualFile? ->
                CiteckVirtualFileUtils.getFileByPath(path)?.let {
                    PsiManager
                        .getInstance(project)
                        .findDirectory(it)
                }
            }
            .orElse(null)

        if (psiDirectory == null) {
            EcosMessages.error("Error", "Cannot get file directory", project)
            return null
        }

        val templates = ArtifactsService.getInstance().getTemplates(meta.id)

        if (templates.size > 1) {
            psiDirectory = psiDirectory.createSubdirectory(targetName)
        }

        return templates.map { template ->

            val file = super.createFileFromTemplate(targetName, template, psiDirectory)

            val fileName = template.name
            if (fileName.contains(".")) {
                val extension = fileName.substring(fileName.indexOf("."))
                PsiFileImplUtil.setName(file, targetName + extension + "." + template.extension)
            }
            file

        }.firstOrNull() ?: error("Template files doesn't found for ${meta.id}")
    }

    private fun cleanExtension(name: String): String {
        val cleanExtension = Stream
            .of(".yml", ".yaml", ".json", ".xml")
            .anyMatch { extension: String? -> name.lowercase(Locale.getDefault()).endsWith(extension!!) }
        if (cleanExtension) {
            return name.substring(0, name.lastIndexOf("."))
        }
        return name
    }

    private fun getArtifactPath(dir: PsiDirectory): String? {
        val module = ModuleUtil.findModuleForPsiElement(dir) ?: return null

        val moduleInfo = CiteckProject.getInstance(module.project).getModuleInfo(module)
        return moduleInfo.artifactsRootPath + "/" + meta.id + "/"
    }

    class Builder {

        var text: String? = null
        var description: String? = null
        var icon: Icon? = null
        lateinit var meta: ArtifactTypeMeta

        fun withText(text: String?): Builder {
            this.text = text
            return this
        }

        fun withDescription(description: String?): Builder {
            this.description = description
            return this
        }

        fun withIcon(icon: Icon?): Builder {
            this.icon = icon
            return this
        }

        fun withMeta(meta: ArtifactTypeMeta): Builder {
            this.meta = meta
            return this
        }

        fun build(): CreateArtifactAction {
            return CreateArtifactAction(
                text,
                description,
                icon,
                meta
            )
        }
    }
}
