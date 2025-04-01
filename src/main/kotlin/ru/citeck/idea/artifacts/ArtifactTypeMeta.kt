package ru.citeck.idea.artifacts

import ecos.com.fasterxml.jackson210.databind.annotation.JsonDeserialize
import ecos.com.fasterxml.jackson210.databind.annotation.JsonPOJOBuilder
import ru.citeck.ecos.commons.data.ObjectData

@JsonDeserialize(builder = ArtifactTypeMeta.Builder::class)
class ArtifactTypeMeta(

    /**
     * Unique identifier of the artifact type.
     * Consists of two parts separated by '/'.
     *
     * Example: "model/type", "ui/menu"
     */
    val typeId: String,

    /**
     * Source identifier used for opening in a browser, performing mutations,
     * and downloading data from the server.
     */
    val sourceId: String,

    /**
     * Public reference source identifier.
     * Defaults to `sourceId` unless explicitly set.
     */
    val publicSourceId: String,

    /**
     * Human-readable name of the artifact type.
     */
    val name: String,

    /**
     * Description of the artifact type.
     */
    val description: String,

    /**
     * Defines how artifacts of this type should be processed.
     * Determines which controller will handle them.
     */
    val kind: ArtifactKind,

    /**
     * Configuration settings for the associated controller.
     */
    val controller: ObjectData,

    /**
     * Set of actions that are disabled for artifacts of this type.
     */
    val disabledActions: Set<String>,

    /**
     * URL pointing to the documentation for this artifact type.
     */
    val docsUrl: String
) {
    companion object {
        fun create(): Builder {
            return Builder()
        }
    }

    fun copy(): Builder {
        return Builder(this)
    }

    @JsonPOJOBuilder
    class Builder() {

        var typeId: String = ""
        var sourceId: String = ""
        var publicSourceId: String = ""
        var name: String = ""
        var description: String = ""
        var kind: ArtifactKind = ArtifactKind.YAML
        var controller: ObjectData = ObjectData.create()
        var disabledActions: Set<String> = emptySet()
        var docsUrl: String = ""

        constructor(base: ArtifactTypeMeta) : this() {
            this.typeId = base.typeId
            this.sourceId = base.sourceId
            this.publicSourceId = base.publicSourceId
            this.name = base.name
            this.description = base.description
            this.kind = base.kind
            this.controller = base.controller.deepCopy()
            this.disabledActions = base.disabledActions
            this.docsUrl = base.docsUrl
        }

        fun withTypeId(typeId: String): Builder {
            this.typeId = typeId
            return this
        }

        fun withSourceId(sourceId: String): Builder {
            this.sourceId = sourceId
            return this
        }

        fun withPublicSourceId(publicSourceId: String): Builder {
            this.publicSourceId = publicSourceId
            return this
        }

        fun withName(name: String): Builder {
            this.name = name
            return this
        }

        fun withDescription(description: String): Builder {
            this.description = description
            return this
        }

        fun withKind(kind: ArtifactKind): Builder {
            this.kind = kind
            return this
        }

        fun withDisabledActions(disabledActions: Set<String>): Builder {
            this.disabledActions = disabledActions
            return this
        }

        fun withDocsUrl(docsUrl: String): Builder {
            this.docsUrl = docsUrl
            return this
        }

        fun build(): ArtifactTypeMeta {
            if (publicSourceId.isBlank()) {
                withPublicSourceId(sourceId)
            }
            return ArtifactTypeMeta(
                typeId = typeId,
                sourceId = sourceId,
                publicSourceId = publicSourceId,
                name = name,
                description = description,
                kind = kind,
                controller = controller,
                disabledActions = disabledActions,
                docsUrl = docsUrl
            )
        }
    }
}
