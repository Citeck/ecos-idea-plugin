package ru.citeck.idea.artifacts

import ecos.com.fasterxml.jackson210.databind.annotation.JsonDeserialize
import ecos.com.fasterxml.jackson210.databind.annotation.JsonPOJOBuilder
import ru.citeck.ecos.commons.data.ObjectData

@JsonDeserialize(builder = ArtifactTypeMeta.Builder::class)
class ArtifactTypeMeta(
    val id: String,
    val sourceId: String,
    val name: String,
    val description: String,
    val type: ArtifactType,
    val controller: ObjectData,
    val disabledActions: Set<String>,
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

        var id: String = ""
        var sourceId: String = ""
        var name: String = ""
        var description: String = ""
        var type: ArtifactType = ArtifactType.YAML
        var controller: ObjectData = ObjectData.create()
        var disabledActions: Set<String> = emptySet()
        var docsUrl: String = ""

        constructor(base: ArtifactTypeMeta) : this() {
            this.id = base.id
            this.sourceId = base.sourceId
            this.name = base.name
            this.description = base.description
            this.controller = base.controller.deepCopy()
            this.disabledActions = base.disabledActions
            this.docsUrl = base.docsUrl
        }

        fun withId(id: String): Builder {
            this.id = id
            return this
        }

        fun withSourceId(id: String): Builder {
            this.sourceId = id
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

        fun withType(type: ArtifactType): Builder {
            this.type = type
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
            return ArtifactTypeMeta(
                id = id,
                sourceId = sourceId,
                name = name,
                description = description,
                type = type,
                controller = controller,
                disabledActions = disabledActions,
                docsUrl = docsUrl
            )
        }
    }
}
