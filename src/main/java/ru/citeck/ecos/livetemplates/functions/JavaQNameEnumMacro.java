package ru.citeck.ecos.livetemplates.functions;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.macro.EnumMacro;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;

public class JavaQNameEnumMacro extends EnumMacro {

    @Override
    public String getName() {
        return "javaQName";
    }

    @Override
    public String getPresentableName() {
        return "javaQName()";
    }

    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression @NotNull [] params, ExpressionContext context) {
        Project project = context.getProject();
        if (project == null) {
            return null;
        }
        return ServiceRegistry.getQNameService(project)
                .createJavaLookupElements()
                .toArray(LookupElement[]::new);
    }

}
