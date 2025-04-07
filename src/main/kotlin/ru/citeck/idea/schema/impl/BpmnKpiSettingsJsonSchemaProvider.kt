package ru.citeck.idea.schema.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import ru.citeck.idea.artifacts.ArtifactsService

class BpmnKpiSettingsJsonSchemaProvider(private val project: Project) : JsonSchemaFileProvider {

    override fun isAvailable(file: VirtualFile): Boolean {
        val meta = ArtifactsService.getInstance().getArtifactInfo(file, project) ?: return false
        return meta.getTypeId() == "process/bpmn-kpi-settings"
    }

    override fun getName(): String = "Citeck BPMN KPI Settings Schema"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            BpmnKpiSettingsJsonSchemaProvider::class.java,
            "/citeck/artifacts/process/bpmn-kpi-settings/bpmn-kpi-settings-schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.schema

    override fun getSchemaVersion(): JsonSchemaVersion = JsonSchemaVersion.SCHEMA_7
} 