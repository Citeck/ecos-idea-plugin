package ru.citeck.idea.project.module

class CiteckModuleInfo(
    val type: CiteckModuleType,
    val artifactsRootPath: String,
    val groupId: String
) {
    companion object {
        var NONE: CiteckModuleInfo = CiteckModuleInfo(
            type = CiteckModuleType.NONE,
            artifactsRootPath = "",
            groupId = ""
        )
    }

    fun isNone(): Boolean {
        return type == CiteckModuleType.NONE
    }
}
