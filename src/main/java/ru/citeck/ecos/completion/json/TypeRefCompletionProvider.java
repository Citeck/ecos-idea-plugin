package ru.citeck.ecos.completion.json;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import icons.Icons;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.types.ecos.model.DataType;

public interface TypeRefCompletionProvider {

    default void createTypeRefsCompletions(@NotNull CompletionResultSet result, Project project) {
        DataType
                .getDataTypes(project)
                .forEach(dataType -> result.addElement(LookupElementBuilder
                        .create(DataType.EMODEL_TYPE + dataType)
                        .withIcon(Icons.CiteckLogo)
                ));
    }

}
