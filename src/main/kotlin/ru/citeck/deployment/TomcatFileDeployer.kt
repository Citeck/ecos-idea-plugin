package ru.citeck.deployment

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.roots.ModuleRootManager
import icons.EcosIcons
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes
import ru.citeck.utils.EcosNotification
import java.io.File
import java.io.FileWriter
import javax.swing.Icon
import javax.swing.JOptionPane

class TomcatFileDeployer : FileDeployer {

    private class Pom {
        lateinit var artifactId: String
    }

    override val icon: Icon get() = EcosIcons.Tomcat


    private fun getTomcatPath(event: AnActionEvent): String {
        val projectDir = event.project!!.guessProjectDir()!!
        val pom = projectDir.findChild("pom.xml")!!.path
        val xmlMapper = XmlMapper()
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val artifactId = xmlMapper.readValue(File(pom), Pom::class.java).artifactId
        return "${projectDir.path}/target/tomcat/webapps/${artifactId}-platform/WEB-INF/classes"
    }


    private fun getRelativePath(event: AnActionEvent): String? {
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return null;
        ModuleManager.getInstance(event.project!!).modules.forEach { module ->
            if (module.name.endsWith("-repo")) {
                val roots = ModuleRootManager.getInstance(module).getSourceRoots(JavaModuleSourceRootTypes.RESOURCES)
                roots.forEach { root ->
                    if (virtualFile.path.startsWith(root.path)) {
                        return virtualFile.path.substring(root.path.length)
                    }
                }
            }
        }
        return null
    }


    override fun canDeploy(event: AnActionEvent): Boolean {
        val vFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return false
        val extension = vFile.extension?.toLowerCase() ?: return false
        if (extension != "js" && extension != "ftl") return false
        return getRelativePath(event) != null
    }


    override fun deploy(event: AnActionEvent) {
        val project = event.project ?: return
        val vFile = event.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        val document = event.getData(PlatformDataKeys.EDITOR)?.document ?: return
        val path = getTomcatPath(event) + getRelativePath(event)

        if (JOptionPane.showConfirmDialog(
                null,
                "Deploy file \"${vFile.name}\" to local Tomcat server?",
                "Deploy file",
                JOptionPane.YES_NO_OPTION
            ) != JOptionPane.YES_OPTION
        ) {
            return
        }

        val fileWriter = FileWriter(path, Charsets.UTF_8)
        fileWriter.write(document.text)
        fileWriter.close()

        EcosNotification.Information("File deployed", "File \"${vFile.name}\" deployed to \"${path}\"", project)
    }


}