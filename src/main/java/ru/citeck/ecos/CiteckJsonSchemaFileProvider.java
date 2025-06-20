package ru.citeck.ecos;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.extension.SchemaType;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.files.FileType;
import ru.citeck.ecos.files.types.ecos.JsonSchemaArtifact;

@RequiredArgsConstructor
public class CiteckJsonSchemaFileProvider implements JsonSchemaFileProvider {

    private final FileType fileType;
    private final Project project;

    @Override
    public boolean isAvailable(@NotNull VirtualFile file) {
        return fileType.accept(file, project);
    }

    @Override
    public @NotNull @Nls String getName() {
        return ((JsonSchemaArtifact) fileType).getSchemaName();
    }

    @Override
    public @Nullable VirtualFile getSchemaFile() {
        return JsonSchemaProviderFactory.getResourceFile(
                CiteckJsonSchemaFileProvider.class,
                "/citeck/schemas/" + ((JsonSchemaArtifact) fileType).getSchemaName()
        );
    }

    @Override
    public @NotNull SchemaType getSchemaType() {
        return SchemaType.schema;
    }

}
