package ru.citeck.idea.authentication

import com.intellij.openapi.project.Project
import ru.citeck.ecos.webapp.api.promise.Promise

interface Authenticator {

    fun getAuthHeader(project: Project, reset: Boolean): Promise<String>
}
