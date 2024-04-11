package ru.citeck.ecos.templates.project;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.wizards.AbstractMavenModuleBuilder;

import javax.swing.*;


public class EcosModuleBuilder extends AbstractMavenModuleBuilder {

    public EcosModuleBuilder() {
    }

    @Override
    public @Nullable ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new EcosModuleWizardStep(this);
    }

    @Override
    public String getPresentableName() {
        return "ECOS";
    }

    @Override
    public String getDescription() {
        return "Create <b>ECOS</b> project";
    }

    @Override
    public Icon getNodeIcon() {
        return Icons.CiteckLogo;
    }

    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[0];
    }

}
