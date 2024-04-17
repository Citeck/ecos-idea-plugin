package ru.citeck.ecos.inspections;

import com.intellij.codeInspection.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.YAMLElementGenerator;
import org.jetbrains.yaml.YAMLUtil;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import ru.citeck.ecos.files.types.ecos.model.DataType;

import java.util.Optional;

public class YamlEcosArtifactTypeRefInspection extends LocalInspectionTool implements EcosArtifactTypeRefInspection {
    @Override
    public @Nullable ProblemDescriptor[] checkFile(@NotNull PsiFile psiFile, @NotNull InspectionManager manager, boolean isOnTheFly) {

        if (!accept(psiFile)) {
            return null;
        }

        YAMLFile yamlFile = (YAMLFile) psiFile;

        YAMLKeyValue typeRef = YAMLUtil
                .getTopLevelKeys(yamlFile)
                .stream()
                .filter(yamlKeyValue -> "typeRef".equals(yamlKeyValue.getKeyText()))
                .findFirst()
                .orElse(null);


        if (typeRef == null) {
            return Optional
                    .of(yamlFile)
                    .map(YAMLFile::getDocuments)
                    .filter(yamlDocuments -> !yamlDocuments.isEmpty())
                    .map(yamlDocuments -> yamlDocuments.get(0))
                    .map(yamlDocument -> manager.createProblemDescriptor(
                            yamlDocument,
                            "\"typeRef\" attribute is mandatory",
                            (LocalQuickFix) null,
                            ProblemHighlightType.ERROR, false)
                    )
                    .map(problemDescriptor -> new ProblemDescriptor[]{problemDescriptor})
                    .orElse(null);
        }

        if (!Strings.isEmpty(typeRef.getValueText())) {
            return null;
        }

        return new ProblemDescriptor[]{
                manager.createProblemDescriptor(
                        typeRef,
                        "\"typeRef\" value is mandatory",
                        new QuickFix(this::fixTypeRef),
                        ProblemHighlightType.ERROR,
                        false
                )};

    }

    public void fixTypeRef(Project project, ProblemDescriptor descriptor, String eType) {
        ApplicationManager.getApplication().runWriteAction(() ->
                CommandProcessor.getInstance().runUndoTransparentAction(() ->
                        Optional
                                .ofNullable(descriptor.getPsiElement())
                                .filter(YAMLKeyValue.class::isInstance)
                                .map(YAMLKeyValue.class::cast)
                                .ifPresent(yamlKeyValue -> yamlKeyValue.replace(
                                        YAMLElementGenerator
                                                .getInstance(project)
                                                .createYamlKeyValue("typeRef", DataType.EMODEL_TYPE + eType)
                                ))
                )
        );
    }

}
