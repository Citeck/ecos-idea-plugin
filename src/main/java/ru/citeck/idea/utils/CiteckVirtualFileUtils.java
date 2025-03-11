package ru.citeck.idea.utils;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.nio.file.Path;

public class CiteckVirtualFileUtils {

    public static VirtualFile getFileByPath(String path) {
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = virtualFileManager.findFileByNioPath(Path.of(path));
        if (virtualFile == null) {
            virtualFile = virtualFileManager.getFileSystem("jar").findFileByPath(path);
        }
        return virtualFile;
    }
}
