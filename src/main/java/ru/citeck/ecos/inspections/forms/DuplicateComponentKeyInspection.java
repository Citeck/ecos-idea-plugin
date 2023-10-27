package ru.citeck.ecos.inspections.forms;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiFile;
import com.jgoodies.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.types.Form;
import ru.citeck.ecos.utils.EcosPsiUtils;

import java.util.Objects;
import java.util.stream.Collectors;

public class DuplicateComponentKeyInspection extends LocalInspectionTool {

    @Override
    public @Nullable ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {

        if (!ServiceRegistry.getFileTypeService().isInstance(file, Form.class)) {
            return null;
        }

        return Form
            .getComponents(file)
            .stream()
            .filter(component -> {
                String type = EcosPsiUtils.getProperty(component, "type");
                return Strings.isNotEmpty(type) && !"column".equals(type);
            })
            .collect(Collectors.groupingBy(component -> EcosPsiUtils.getProperty(component, "key")))
            .entrySet()
            .stream()
            .filter(e -> e.getValue().size() > 1)
            .flatMap(e -> e.getValue().stream())
            .map(component -> component.findProperty("key"))
            .filter(Objects::nonNull)
            .map(componenyKey -> manager.createProblemDescriptor(
                componenyKey,
                "Duplicate component key",
                (LocalQuickFix) null,
                ProblemHighlightType.ERROR,
                false)
            )
            .toArray(ProblemDescriptor[]::new);

    }

}
