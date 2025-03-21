package ru.citeck.idea.inspections;

import com.intellij.codeInspection.*;
import com.intellij.json.psi.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.idea.files.types.citeck.model.DataType;

import java.util.Optional;

public class JsonCiteckArtifactTypeRefInspection extends LocalInspectionTool implements EcosArtifactTypeRefInspection {

    @Override
    public @Nullable ProblemDescriptor[] checkFile(@NotNull PsiFile psiFile, @NotNull InspectionManager manager, boolean isOnTheFly) {

        if (!accept(psiFile)) {
            return null;
        }

        JsonObject jsonObject = ((PsiFileImpl) psiFile).findChildByClass(JsonObject.class);
        if (jsonObject == null) {
            return null;
        }
        JsonProperty typeRef = jsonObject.findProperty("typeRef");
        if (typeRef == null) {
            return new ProblemDescriptor[]{manager.createProblemDescriptor(
                jsonObject,
                "\"typeRef\" attribute is mandatory",
                (LocalQuickFix) null,
                ProblemHighlightType.ERROR, false)
            };
        }

        String typeRefValue = Optional
            .ofNullable(typeRef.getValue())
            .map(PsiElement::getText)
            .map(JsonPsiUtil::stripQuotes)
            .orElse("");

        if (!Strings.isEmpty(typeRefValue)) {
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
            CommandProcessor.getInstance().runUndoTransparentAction(() -> Optional
                .ofNullable(descriptor.getPsiElement())
                .filter(JsonProperty.class::isInstance)
                .map(JsonProperty.class::cast)
                .map(JsonProperty::getValue)
                .ifPresent(jsonValue -> {
                    JsonValue newValue = new JsonElementGenerator(project)
                        .createValue("\"" + DataType.EMODEL_TYPE + eType + "\"");
                    jsonValue.replace(newValue);
                }))
        );
    }

}
