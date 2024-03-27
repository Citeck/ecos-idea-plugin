package ru.citeck.ecos.templates.files;


import com.intellij.util.xmlb.annotations.Attribute;
import lombok.Getter;

@Getter
public class EcosArtifactTemplate {

    @Attribute("template")
    public String template;

    @Attribute("path")
    public String path;

    @Attribute("kind")
    public String kind;

}
