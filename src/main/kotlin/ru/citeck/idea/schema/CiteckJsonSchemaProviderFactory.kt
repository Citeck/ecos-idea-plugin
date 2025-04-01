package ru.citeck.idea.schema

import com.intellij.openapi.project.Project
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import ru.citeck.idea.schema.impl.AspectJsonSchemaProvider
import ru.citeck.idea.schema.impl.EndpointJsonSchemaProvider
import ru.citeck.idea.schema.impl.JournalJsonSchemaProvider
import ru.citeck.idea.schema.impl.ModelTypeJsonSchemaProvider
import ru.citeck.idea.schema.impl.NumberTemplateJsonSchemaProvider
import ru.citeck.idea.schema.impl.PermissionDefJsonSchemaProvider
import ru.citeck.idea.schema.impl.PermissionsJsonSchemaProvider
import ru.citeck.idea.schema.impl.SecretJsonSchemaProvider
import ru.citeck.idea.schema.impl.WorkingCalendarJsonSchemaProvider

class CiteckJsonSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(
            ModelTypeJsonSchemaProvider(project),
            JournalJsonSchemaProvider(project),
            AspectJsonSchemaProvider(project),
            EndpointJsonSchemaProvider(project),
            NumberTemplateJsonSchemaProvider(project),
            PermissionDefJsonSchemaProvider(project),
            PermissionsJsonSchemaProvider(project),
            SecretJsonSchemaProvider(project),
            WorkingCalendarJsonSchemaProvider(project)
        )
    }
}
