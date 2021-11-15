package ru.citeck.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import ru.citeck.deployment.EcosUiFileDeployer
import ru.citeck.deployment.FileDeployer
import ru.citeck.deployment.TomcatFileDeployer

class DeployFile : AnAction() {

    private val deployers = listOf(
        EcosUiFileDeployer(),
        TomcatFileDeployer()
    )

    override fun update(event: AnActionEvent) {
        event.presentation.isVisible = false
        val deployer = getDeployer(event) ?: return
        event.presentation.isVisible = true
        event.presentation.icon = deployer.icon
    }

    private fun getDeployer(event: AnActionEvent): FileDeployer? {
        deployers.forEach { deployer ->
            if (deployer.canDeploy(event)) {
                return deployer
            }
        }
        return null
    }

    override fun actionPerformed(event: AnActionEvent) {
        getDeployer(event)!!.deploy(event)
    }

}