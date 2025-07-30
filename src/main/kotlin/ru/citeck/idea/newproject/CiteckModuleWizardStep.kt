package ru.citeck.idea.newproject

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import icons.Icons
import org.jetbrains.idea.maven.model.MavenId
import ru.citeck.idea.view.list.ListCellRendererWithIcon
import java.awt.GridLayout
import javax.swing.JComponent

class CiteckModuleWizardStep(private val builder: CiteckModuleBuilder) : ModuleWizardStep() {

    private val groupId = JBTextField("ru.citeck.webapp.custom")
    private val artifactId = JBTextField("custom")
    private val version = JBTextField("1.0.0-SNAPSHOT")
    private val port = JBTextField("8686")

    private val archetypesList = createArchetypesList()

    private fun createArchetypesList(): JBList<CiteckMavenArchetype> {
        val archetypesList = JBList(CiteckMavenArchetype.EP_NAME.extensionList)
        archetypesList.cellRenderer = ListCellRendererWithIcon(Icons.CiteckLogo)
        archetypesList.border = JBUI.Borders.empty(0, 4)
        archetypesList.addListSelectionListener {
            port.isEnabled = java.lang.Boolean.TRUE == archetypesList.selectedValue.isMicroservice
        }
        if (!archetypesList.isEmpty) {
            archetypesList.selectedIndex = 0
        }
        return archetypesList
    }

    override fun getComponent(): JComponent {
        val panel = FormBuilder
            .createFormBuilder()
            .setFormLeftIndent(8)
            .addLabeledComponentFillVertically("Project type:", archetypesList)
            .addLabeledComponent("Group id", groupId, false)
            .addLabeledComponent("Artifact id:", artifactId, false)
            .addLabeledComponent("Version: ", version, false)
            .addLabeledComponent("Port: ", port, false)
            .panel

        val wrapper: JBPanel<*> = JBPanel<JBPanel<*>>(GridLayout(1, 1))
        wrapper.border = JBUI.Borders.empty(6)
        wrapper.add(panel)

        return wrapper
    }

    override fun updateDataModel() {
        val archetype = archetypesList.selectedValue.getMavenArchetype()
        builder.propertiesToCreateByArtifact = mapOf(
            "groupId" to groupId.text,
            "artifactId" to artifactId.text,
            "version" to version.text,
            "archetypeGroupId" to archetype.groupId,
            "archetypeArtifactId" to archetype.artifactId,
            "archetypeVersion" to archetype.version,
            "port" to port.text
        )
        builder.projectId = MavenId(groupId.text, artifactId.text, version.text)
        builder.archetype = archetype
    }
}
