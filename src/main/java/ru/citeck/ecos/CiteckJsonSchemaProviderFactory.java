package ru.citeck.ecos;

import com.intellij.openapi.project.Project;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.files.FileTypeService;
import ru.citeck.ecos.files.types.ecos.JsonSchemaArtifact;

import java.util.List;
import java.util.stream.Collectors;

public class CiteckJsonSchemaProviderFactory implements JsonSchemaProviderFactory {

    @Override
    public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        return FileTypeService.EP_NAME
                .getExtensionsIfPointIsRegistered()
                .stream()
                .filter(JsonSchemaArtifact.class::isInstance)
                .map(fileType -> new CiteckJsonSchemaFileProvider(fileType, project))
                .collect(Collectors.toList());
    }
}
