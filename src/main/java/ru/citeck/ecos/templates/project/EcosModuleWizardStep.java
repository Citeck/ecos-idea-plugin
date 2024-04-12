package ru.citeck.ecos.templates.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import icons.Icons;
import org.jetbrains.idea.maven.model.MavenArchetype;
import org.jetbrains.idea.maven.model.MavenId;
import ru.citeck.ecos.ui.ListCellRendererWithIcon;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class EcosModuleWizardStep extends ModuleWizardStep {

    private final EcosModuleBuilder builder;

    private final JBList<EcosMavenArchetype> archetypesList = createArchetypesList();

    private final JBTextField groupId = new JBTextField("org.example");
    private final JBTextField artifactId = new JBTextField("untitled");
    private final JBTextField version = new JBTextField("1.0.0-SNAPSHOT");
    private final JBTextField port = new JBTextField("8686");

    public EcosModuleWizardStep(EcosModuleBuilder builder) {
        this.builder = builder;
    }

    private JBList<EcosMavenArchetype> createArchetypesList() {
        JBList<EcosMavenArchetype> archetypesList = new JBList<>(EcosMavenArchetype.EP_NAME.getExtensionList());
        archetypesList.setCellRenderer(new ListCellRendererWithIcon(Icons.CiteckLogo));
        archetypesList.setBorder(JBUI.Borders.empty(0, 4));
        archetypesList.addListSelectionListener(e -> port
                .setEnabled(Boolean.TRUE.equals(archetypesList.getSelectedValue().isMicroservice))
        );
        if (!archetypesList.isEmpty()) {
            archetypesList.setSelectedIndex(0);
        }
        return archetypesList;
    }

    @Override
    public JComponent getComponent() {

        JPanel panel = FormBuilder
                .createFormBuilder()
                .setFormLeftIndent(8)
                .addLabeledComponentFillVertically("Project type:", archetypesList)
                .addLabeledComponent("Group id", groupId, false)
                .addLabeledComponent("Artifact id:", artifactId, false)
                .addLabeledComponent("Version: ", version, false)
                .addLabeledComponent("Port: ", port, false)
                .getPanel();

        JBPanel<?> wrapper = new JBPanel<>(new GridLayout(1, 1));
        wrapper.setBorder(JBUI.Borders.empty(6));
        wrapper.add(panel);

        return wrapper;

    }

    @Override
    public void updateDataModel() {
        MavenArchetype archetype = archetypesList.getSelectedValue().getMavenArchetype();
        builder.setPropertiesToCreateByArtifact(Map.of(
                "groupId", groupId.getText(),
                "artifactId", artifactId.getText(),
                "version", version.getText(),
                "archetypeGroupId", archetype.groupId,
                "archetypeArtifactId", archetype.artifactId,
                "archetypeVersion", archetype.version,
                "port", port.getText()
        ));
        builder.setProjectId(new MavenId(groupId.getText(), artifactId.getText(), version.getText()));
        builder.setArchetype(archetype);
    }
}
