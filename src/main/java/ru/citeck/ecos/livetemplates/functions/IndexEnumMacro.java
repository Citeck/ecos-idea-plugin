package ru.citeck.ecos.livetemplates.functions;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.macro.EnumMacro;
import com.intellij.openapi.project.Project;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexesService;

import javax.swing.*;
import java.util.List;

public abstract class IndexEnumMacro extends EnumMacro {

    public Icon getIcon() {
        return Icons.CiteckLogo;
    }

    abstract public List<IndexKey> getIndexKeys();

    @Override
    abstract public String getName();

    @Override
    abstract public String getPresentableName();

    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression[] params, ExpressionContext context) {

        Project project = context.getProject();
        if (project == null) {
            return null;
        }

        IndexesService indexesService = ServiceRegistry.getIndexesService(project);

        return getIndexKeys()
            .stream()
            .flatMap(key -> indexesService
                .stream(key)
                .map(indexValue -> LookupElementBuilder
                    .create(indexValue.getId())
                    .withIcon(getIcon())
                    .withTypeText(key.getType())
                )
            )
            .toArray(LookupElement[]::new);

    }

}