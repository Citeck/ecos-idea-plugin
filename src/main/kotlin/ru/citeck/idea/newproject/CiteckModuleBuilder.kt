package ru.citeck.idea.newproject

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.DumbAwareRunnable
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import icons.Icons
import org.jetbrains.idea.maven.execution.MavenRunner
import org.jetbrains.idea.maven.execution.MavenRunnerParameters
import org.jetbrains.idea.maven.model.MavenConstants
import org.jetbrains.idea.maven.utils.MavenUtil
import org.jetbrains.idea.maven.wizards.AbstractMavenModuleBuilder
import org.jetbrains.idea.maven.wizards.MavenOpenProjectProvider
import ru.citeck.idea.utils.CiteckVirtualFileUtils.getFileByPath
import java.io.File
import java.io.IOException
import java.util.*
import javax.swing.Icon
import kotlin.collections.emptyList
import kotlin.collections.set

class CiteckModuleBuilder : AbstractMavenModuleBuilder() {

    companion object {
        const val MAVEN_PLUGIN_GENERATE_ARCHETYPE: String =
            "org.apache.maven.plugins:maven-archetype-plugin:2.4:generate"
        const val REPOSITORY: String = "https://nexus.citeck.ru/repository/maven-public/"
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep {
        return CiteckModuleWizardStep(this)
    }

    override fun getPresentableName(): String {
        return "Citeck"
    }

    override fun getDescription(): String {
        return "Create <b>Citeck</b> project"
    }

    override fun getNodeIcon(): Icon {
        return Icons.CiteckLogo
    }

    override fun setupModule(module: Module) {
        val project = module.project
        MavenUtil.runWhenInitialized(
            project,
            DumbAwareRunnable {
                ApplicationManager.getApplication().invokeLater {
                    try {
                        generateMavenArchetype(module)
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            })
    }

    private fun generateMavenArchetype(module: Module) {
        val project = module.project
        val moduleDir = getFileByPath(
            moduleFileDirectory!!
        )

        val tmpDir = FileUtil.createTempDirectory("archetype", "tmp")
        tmpDir.deleteOnExit()

        val mavenParams = MavenRunnerParameters(
            false, tmpDir.path, null as String?,
            listOf(MAVEN_PLUGIN_GENERATE_ARCHETYPE),
            emptyList()
        )

        val runner = MavenRunner.getInstance(project)
        val mavenProperties = runner.settings.mavenProperties
        mavenProperties.putAll(this.propertiesToCreateByArtifact)
        mavenProperties["archetypeCatalog"] = REPOSITORY
        mavenProperties["interactiveMode"] = "false"

        runner.run(mavenParams, runner.state.clone()) {
            try {
                val artifactId = propertiesToCreateByArtifact["artifactId"] ?: "unknown"
                FileUtil.copyDir(
                    File(tmpDir, artifactId),
                    File(moduleDir!!.path)
                )
                FileUtil.delete(tmpDir)
                moduleDir.refresh(false, false)
                Optional
                    .ofNullable(moduleDir.findChild(MavenConstants.POM_XML))
                    .ifPresent { pom: VirtualFile? ->
                        MavenOpenProjectProvider().linkToExistingProject(
                            pom!!, project
                        )
                    }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }

    override fun createWizardSteps(
        wizardContext: WizardContext,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
        return emptyArray()
    }
}
