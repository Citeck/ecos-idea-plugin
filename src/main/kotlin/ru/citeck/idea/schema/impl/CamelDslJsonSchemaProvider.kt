package ru.citeck.idea.schema.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import ru.citeck.idea.artifacts.ArtifactsService

class CamelDslJsonSchemaProvider(private val project: Project) : JsonSchemaFileProvider {

    override fun isAvailable(file: VirtualFile): Boolean {
        val meta = ArtifactsService.getInstance().getArtifactInfo(file, project) ?: return false
        return meta.getTypeId() == "integration/camel-dsl"
    }

    override fun getName(): String = "Citeck Camel DSL Schema"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            CamelDslJsonSchemaProvider::class.java,
            "/citeck/artifacts/integration/camel-dsl/camel-dsl-schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.schema

    override fun getSchemaVersion(): JsonSchemaVersion = JsonSchemaVersion.SCHEMA_7
} 