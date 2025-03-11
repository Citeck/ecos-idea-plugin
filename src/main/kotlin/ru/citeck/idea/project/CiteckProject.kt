package ru.citeck.idea.project

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessModuleDir
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.xml.XmlFile
import ru.citeck.idea.project.module.CiteckModuleInfo
import ru.citeck.idea.project.module.CiteckModuleType
import ru.citeck.idea.utils.MavenUtils
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.PROJECT)
class CiteckProject(
    private val project: Project
) : Disposable {

    companion object {

        private val log = Logger.getInstance(CiteckProject::class.java)

        fun getInstance(project: Project): CiteckProject {
            return project.getService(CiteckProject::class.java)
        }
    }

    private val modulesInfo: MutableMap<Module, CiteckModuleInfo> = ConcurrentHashMap()

    init {
        project.messageBus
            .connect(this)
            .subscribe(VirtualFileManager.VFS_CHANGES, CiteckModulePomListener())
    }

    fun isCiteckModule(module: Module?): Boolean {
        return CiteckModuleType.NONE != getModuleInfo(module).type
    }

    fun getModuleInfo(module: Module?): CiteckModuleInfo {
        if (module == null) {
            return CiteckModuleInfo.NONE
        }
        return modulesInfo.computeIfAbsent(module) { moduleKey: Module ->

            val moduleDir = moduleKey.guessModuleDir() ?: return@computeIfAbsent CiteckModuleInfo.NONE
            val groupId = getParentGroupIdFromPomFile(MavenUtils.getPomFile(moduleKey))

            val moduleType = CiteckModuleType.findByGroupId(groupId)
            val artifactsRootPath = moduleDir.path + moduleType.artifactsRoot

            CiteckModuleInfo(
                CiteckModuleType.findByGroupId(groupId),
                artifactsRootPath,
                groupId
            )
        }
    }

    override fun dispose() {
        modulesInfo.clear()
    }

    private fun getParentGroupIdFromPomFile(pomFile: XmlFile?): String {
        return pomFile?.rootTag?.findFirstSubTag("parent")
            ?.findFirstSubTag("groupId")
            ?.value
            ?.text ?: ""
    }

    private inner class CiteckModulePomListener : BulkFileListener {

        override fun after(events: List<VFileEvent>) {
            for (event in events) {
                val file = event.file
                if (file == null || "pom.xml" != file.name) {
                    continue
                }
                val module = ModuleUtil.findModuleForFile(file, project) ?: continue
                val currentModuleInfo = modulesInfo[module] ?: continue
                val groupId = Optional.of(PsiUtil.getPsiFile(project, file))
                    .filter { obj: PsiFile? -> XmlFile::class.java.isInstance(obj) }
                    .map { obj: PsiFile? -> XmlFile::class.java.cast(obj) }
                    .map { pomFile: XmlFile ->
                        this@CiteckProject.getParentGroupIdFromPomFile(
                            pomFile
                        )
                    }
                    .orElse("")

                if (groupId != currentModuleInfo.groupId) {
                    log.info("Module group id was changed. Clean cache for module " + module.name)
                    modulesInfo.remove(module)
                }
            }
        }
    }
}
