package ru.citeck.ecos.templates.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBUI;
import icons.Icons;
import org.jetbrains.idea.maven.model.MavenArchetype;
import org.jetbrains.idea.maven.model.MavenId;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class EcosModuleWizardStep extends ModuleWizardStep {

    private final EcosModuleBuilder builder;

    private final JBList<EcosMavenArchetype> archetypesList = new JBList<>(EcosMavenArchetype.EP_NAME.getExtensionList());
    private final JBTextField groupId = new JBTextField();
    private final JBTextField artifactId = new JBTextField();
    private final JBTextField version = new JBTextField();
    private final JBTextField port = new JBTextField();

    public EcosModuleWizardStep(EcosModuleBuilder builder) {
        this.builder = builder;
        groupId.setText("org.example");
        artifactId.setText("untitled");
        version.setText("1.0-SNAPSHOT");
        port.setText("8686");
    }

    @Override
    public JComponent getComponent() {

        JPanel root = new JBPanel<>(new GridLayout(2, 1));

        archetypesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Icon getIcon() {
                return Icons.CiteckLogo;
            }

            @Override
            public int getIconTextGap() {
                return 4;
            }

        });
        if (!archetypesList.isEmpty()) {
            archetypesList.setSelectedIndex(0);
        }
        root.add(archetypesList);

        JPanel artifactProperties = new JBPanel<>(new VerticalLayout(0));
        JPanel panelLeft = new JBPanel<>(new GridLayout(1, 2));
        panelLeft.add(artifactProperties);
        panelLeft.add(new JBPanel<>());

        root.add(panelLeft);

        addComponentWithLabel("Group id:", artifactProperties, groupId);
        addComponentWithLabel("Artifact id:", artifactProperties, artifactId);
        addComponentWithLabel("Version: ", artifactProperties, version);
        addComponentWithLabel("Port: ", artifactProperties, port);

        return root;
    }

    private void addComponentWithLabel(String label, JPanel parent, Component component) {
        JPanel panel = new JBPanel<>();
        panel.setLayout(new VerticalLayout(0));

        panel.add(new JBLabel(label));
        panel.add(component);
        panel.setBorder(JBUI.Borders.empty(10, 10, 0, 0));

        parent.add(panel);
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
