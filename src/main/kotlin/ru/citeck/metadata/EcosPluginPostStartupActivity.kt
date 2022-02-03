package ru.citeck.metadata


import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import ru.citeck.metadata.providers.ModelsProvider
import ru.citeck.metadata.providers.QNamesProvider

class EcosPluginPostStartupActivity : StartupActivity {

    private val metadataProviders = arrayListOf(
        ModelsProvider::class.java,
        QNamesProvider::class.java
    )

    override fun runActivity(project: Project) {
        initMetadataProviders(project)
    }

    private fun initMetadataProviders(project: Project) {

        DumbService.getInstance(project).smartInvokeLater {
            runBackgroundableTask("Loading Ecos metadata", project) {
                it.isIndeterminate = true
                for (i in metadataProviders.indices) {
                    it.fraction = (i / metadataProviders.size.toDouble())
                    project.getService(metadataProviders[i]).initialize()
                }
            }
        }

    }

}


