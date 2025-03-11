package ru.citeck.idea.templates.project;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.idea.maven.model.MavenArchetype;

public class CiteckMavenArchetype {

    public static final ExtensionPointName<CiteckMavenArchetype> EP_NAME =
        ExtensionPointName.create("ru.citeck.idea.citeckMavenArchetype");

    @Attribute("displayName")
    public String displayName;

    @Attribute("groupId")
    public String groupId;

    @Attribute("artifactId")
    public String artifactId;

    @Attribute("version")
    public String version;

    @Attribute("description")
    public String description;

    @Attribute("isMicroservice")
    public Boolean isMicroservice;

    public MavenArchetype getMavenArchetype() {
        return new MavenArchetype(groupId, artifactId, version, null, description);
    }

    @Override
    public String toString() {
        return displayName;
    }

}
