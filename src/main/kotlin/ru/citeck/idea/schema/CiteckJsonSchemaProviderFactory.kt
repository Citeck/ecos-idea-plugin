package ru.citeck.idea.schema

import com.intellij.openapi.project.Project
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import ru.citeck.idea.schema.impl.JournalJsonSchemaProvider
import ru.citeck.idea.schema.impl.ModelTypeJsonSchemaProvider

class CiteckJsonSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(
            ModelTypeJsonSchemaProvider(project),
            JournalJsonSchemaProvider(project)
        )
    }
}
