package ru.citeck.idea.schema

import com.intellij.openapi.project.Project
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import ru.citeck.idea.schema.impl.*

class CiteckJsonSchemaProviderFactory : JsonSchemaProviderFactory {

    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        return listOf(
            ActionJsonSchemaProvider(project),
            AspectJsonSchemaProvider(project),
            BoardJsonSchemaProvider(project),
            BpmnKpiSettingsJsonSchemaProvider(project),
            BpmnTaskAttsSyncJsonSchemaProvider(project),
            CamelDslJsonSchemaProvider(project),
            EndpointJsonSchemaProvider(project),
            JournalJsonSchemaProvider(project),
            ModelTypeJsonSchemaProvider(project),
            NotificationTemplateJsonSchemaProvider(project),
            NumberTemplateJsonSchemaProvider(project),
            PermissionDefJsonSchemaProvider(project),
            PermissionsJsonSchemaProvider(project),
            ReminderJsonSchemaProvider(project),
            SecretJsonSchemaProvider(project),
            SenderJsonSchemaProvider(project),
            TemplateJsonSchemaProvider(project),
            WorkingCalendarJsonSchemaProvider(project),
            WorkingScheduleJsonSchemaProvider(project),
            WorkspaceJsonSchemaProvider(project)
        )
    }
}
