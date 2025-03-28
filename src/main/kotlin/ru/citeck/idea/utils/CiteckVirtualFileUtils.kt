package ru.citeck.idea.utils

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import java.nio.file.Path

object CiteckVirtualFileUtils {

    @JvmStatic
    fun getFileByPath(path: String): VirtualFile? {
        val virtualFileManager = VirtualFileManager.getInstance()
        var virtualFile = virtualFileManager.findFileByNioPath(Path.of(path))
        if (virtualFile == null) {
            virtualFile = virtualFileManager.getFileSystem("jar").findFileByPath(path)
        }
        return virtualFile
    }
}
