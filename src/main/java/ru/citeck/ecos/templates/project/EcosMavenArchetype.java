package ru.citeck.ecos.templates.project;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.idea.maven.model.MavenArchetype;

public class EcosMavenArchetype {

    public static final ExtensionPointName<EcosMavenArchetype> EP_NAME =
            ExtensionPointName.create("ru.citeck.ecos.ecosMavenArchetype");

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
