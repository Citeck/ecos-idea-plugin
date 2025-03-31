package ru.citeck.idea.schema.impl

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory
import com.jetbrains.jsonSchema.extension.SchemaType
import com.jetbrains.jsonSchema.impl.JsonSchemaVersion
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import ru.citeck.idea.artifacts.ArtifactsService

class JournalJsonSchemaProvider(private val project: Project) : JsonSchemaFileProvider {

    override fun isAvailable(file: VirtualFile): Boolean {
        val meta = ArtifactsService.getInstance().getArtifactTypeMeta(file.toPsiFile(project)) ?: return false
        return meta.id == "ui/journal"
    }

    override fun getName(): String = "ECOS Journal Schema"

    override fun getSchemaFile(): VirtualFile? {
        return JsonSchemaProviderFactory.getResourceFile(
            JournalJsonSchemaProvider::class.java,
            "/citeck/artifacts/ui/journal/journal-schema.json"
        )
    }

    override fun getSchemaType(): SchemaType = SchemaType.schema

    override fun getSchemaVersion(): JsonSchemaVersion = JsonSchemaVersion.SCHEMA_7
} 