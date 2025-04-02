package ru.citeck.idea.schema

import com.intellij.openapi.project.Project
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import ru.citeck.idea.schema.impl.ActionJsonSchemaProvider
import ru.citeck.idea.schema.impl.AspectJsonSchemaProvider
import ru.citeck.idea.schema.impl.BoardJsonSchemaProvider
import ru.citeck.idea.schema.impl.EndpointJsonSchemaProvider
import ru.citeck.idea.schema.impl.JournalJsonSchemaProvider
import ru.citeck.idea.schema.impl.ModelTypeJsonSchemaProvider
import ru.citeck.idea.schema.impl.NumberTemplateJsonSchemaProvider
import ru.citeck.idea.schema.impl.PermissionDefJsonSchemaProvider
import ru.citeck.idea.schema.impl.PermissionsJsonSchemaProvider
import ru.citeck.idea.schema.impl.SecretJsonSchemaProvider
import ru.citeck.idea.schema.impl.WorkingCalendarJsonSchemaProvider
import ru.citeck.idea.schema.impl.WorkingScheduleJsonSchemaProvider
import ru.citeck.idea.schema.impl.WorkspaceJsonSchemaProvider

class CiteckJsonSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(
            ActionJsonSchemaProvider(project),
            AspectJsonSchemaProvider(project),
            BoardJsonSchemaProvider(project),
            EndpointJsonSchemaProvider(project),
            JournalJsonSchemaProvider(project),
            ModelTypeJsonSchemaProvider(project),
            NumberTemplateJsonSchemaProvider(project),
            PermissionDefJsonSchemaProvider(project),
            PermissionsJsonSchemaProvider(project),
            SecretJsonSchemaProvider(project),
            WorkingCalendarJsonSchemaProvider(project),
            WorkingScheduleJsonSchemaProvider(project),
            WorkspaceJsonSchemaProvider(project)
        )
    }
}
