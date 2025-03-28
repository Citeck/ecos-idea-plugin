package ru.citeck.idea

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import ru.citeck.idea.artifacts.ArtifactsMetaLoader

/**
 * Service required to allow use Disposer.register(CiteckPlugin.getInstance(), disposable)
 */
@Service(Service.Level.APP)
class CiteckPlugin : Disposable {

    companion object {

        var ID: String = "ru.citeck.idea"

        private val log = Logger.getInstance(CiteckPlugin::class.java)

        @JvmStatic
        fun getInstance(): CiteckPlugin {
            return ApplicationManager.getApplication().getService(CiteckPlugin::class.java)
        }
    }

    override fun dispose() {
        log.info("Disposing CiteckPlugin")
    }

    class Initializer : ProjectActivity {
        override suspend fun execute(project: Project) {
            // Services initialized lazily. To force artifacts loading call getService.
            ApplicationManager.getApplication().getService(ArtifactsMetaLoader::class.java)
        }
    }
}
