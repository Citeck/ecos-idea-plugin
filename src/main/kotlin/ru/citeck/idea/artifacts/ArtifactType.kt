package ru.citeck.idea.artifacts

import com.intellij.icons.AllIcons
import javax.swing.Icon

enum class ArtifactType(
    val icon: Icon,
    val extension: String
) {
    JSON(AllIcons.FileTypes.Json, "json"),
    YAML(AllIcons.FileTypes.Yaml, "yml"),
    BPMN(AllIcons.FileTypes.Xml, "bpmn.xml"),
    DMN(AllIcons.FileTypes.Xml, "dmn.xml"),
    NOTIFICATION_TEMPLATE(AllIcons.FileTypes.Yaml, "yml");
}
