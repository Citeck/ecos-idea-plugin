package ru.citeck.ecos.livetemplates.functions;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.macro.EnumMacro;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexesService;

import java.util.List;

public abstract class IndexEnumMacro extends EnumMacro {

    abstract public List<IndexKey> getIndexKeys();

    @Override
    abstract public String getName();

    @Override
    abstract public String getPresentableName();

    @Override
    public LookupElement[] calculateLookupItems(@NotNull Expression @NotNull [] params, ExpressionContext context) {

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
                                .withIcon(indexValue.getIcon())
                                .withTypeText(key.getType())
                        )
                )
                .toArray(LookupElement[]::new);

    }

}
