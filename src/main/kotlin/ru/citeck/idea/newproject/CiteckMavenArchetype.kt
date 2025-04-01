package ru.citeck.idea.newproject

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.util.xmlb.annotations.Attribute
import org.jetbrains.idea.maven.model.MavenArchetype

class CiteckMavenArchetype {

    companion object {
        val EP_NAME: ExtensionPointName<CiteckMavenArchetype> =
            ExtensionPointName.create("ru.citeck.idea.citeckMavenArchetype")
    }

    @Attribute("displayName")
    lateinit var displayName: String

    @Attribute("groupId")
    lateinit var groupId: String

    @Attribute("artifactId")
    lateinit var artifactId: String

    @Attribute("version")
    lateinit var version: String

    @Attribute("description")
    var description: String? = null

    @Attribute("isMicroservice")
    var isMicroservice: Boolean = false

    fun getMavenArchetype(): MavenArchetype {
        return MavenArchetype(groupId, artifactId, version, null, description)
    }

    override fun toString(): String {
        return displayName
    }
}
