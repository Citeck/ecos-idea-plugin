package ru.citeck.idea.templates.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunner;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.AbstractMavenModuleBuilder;
import org.jetbrains.idea.maven.wizards.MavenOpenProjectProvider;
import ru.citeck.idea.utils.CiteckVirtualFileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CiteckModuleBuilder extends AbstractMavenModuleBuilder {

    public static final String MAVEN_PLUGIN_GENERATE_ARCHETYPE = "org.apache.maven.plugins:maven-archetype-plugin:2.4:generate";
    public static final String REPOSITORY = "https://nexus.citeck.ru/repository/maven-public/";

    public CiteckModuleBuilder() {
    }

    @Override
    public @Nullable ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new EcosModuleWizardStep(this);
    }

    @Override
    public String getPresentableName() {
        return "Citeck";
    }

    @Override
    public String getDescription() {
        return "Create <b>Citeck</b> project";
    }

    @Override
    public Icon getNodeIcon() {
        return Icons.CiteckLogo;
    }

    @Override
    protected void setupModule(Module module) {
        Project project = module.getProject();
        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () ->
            ApplicationManager.getApplication().invokeLater(() -> {
                try {
                    generateMavenArchetype(module);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    private void generateMavenArchetype(Module module) throws IOException {

        Project project = module.getProject();
        VirtualFile moduleDir = CiteckVirtualFileUtils.getFileByPath(getModuleFileDirectory());

        File tmpDir = FileUtil.createTempDirectory("archetype", "tmp");
        tmpDir.deleteOnExit();

        MavenRunnerParameters mavenParams = new MavenRunnerParameters(
            false, tmpDir.getPath(), (String) null,
            List.of(MAVEN_PLUGIN_GENERATE_ARCHETYPE),
            Collections.emptyList());

        MavenRunner runner = MavenRunner.getInstance(project);
        Map<String, String> mavenProperties = runner.getSettings().getMavenProperties();
        mavenProperties.putAll(this.getPropertiesToCreateByArtifact());
        mavenProperties.put("archetypeCatalog", REPOSITORY);
        mavenProperties.put("interactiveMode", "false");

        runner.run(mavenParams, runner.getState().clone(), () -> {
            try {
                String artifactId = getPropertiesToCreateByArtifact().get("artifactId");
                FileUtil.copyDir(new File(tmpDir, artifactId), new File(moduleDir.getPath()));
                FileUtil.delete(tmpDir);
                moduleDir.refresh(false, false);
                Optional
                    .ofNullable(moduleDir.findChild(MavenConstants.POM_XML))
                    .ifPresent(pom -> new MavenOpenProjectProvider().linkToExistingProject(pom, project));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext,
                                                @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[0];
    }
}
