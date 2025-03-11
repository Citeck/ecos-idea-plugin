package ru.citeck.idea.project.module

enum class CiteckModuleType(
    val groupId: String,
    val artifactsRoot: String
) {

    ECOS_APP("ru.citeck.ecos.eapps.project", "/src/main/resources/app/artifacts"),
    WEBAPP("ru.citeck.ecos.webapp", "/src/main/resources/eapps/artifacts"),
    NONE("", "");

    companion object {
        fun findByGroupId(groupId: String?): CiteckModuleType {
            if (groupId.isNullOrEmpty()) {
                return NONE
            }
            for (moduleType in values()) {
                if (moduleType.groupId == groupId) {
                    return moduleType
                }
            }
            return NONE
        }
    }
}
