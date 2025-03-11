package ru.citeck.idea.utils

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiDirectory
import com.intellij.util.io.Decompressor
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.util.*

object CiteckZipContentUtil {

    @JvmStatic
    fun applyBase64ZipContentToDirectory(psiDirectory: PsiDirectory, content: String) {

        val tmpDir = FileUtil.createTempDirectory("ecos-content", "tmp")
        tmpDir.deleteOnExit()

        val contentZip = File(tmpDir, "content.zip")

        val fos = FileOutputStream(contentZip)
        fos.write(Base64.getDecoder().decode(content))
        fos.close()

        val decompressor: Decompressor = Decompressor.Zip(contentZip)
        decompressor.extract(tmpDir)

        FileUtil.delete(contentZip)

        val vDirectory = psiDirectory.virtualFile
        val virtualFileManager = VirtualFileManager.getInstance()

        val files = Optional
            .ofNullable(tmpDir.listFiles())
            .stream()
            .flatMap { array: Array<File>? -> Arrays.stream(array) }
            .toList()

        for (file in files) {
            if (file.isDirectory) {
                psiDirectory.createSubdirectory(file.name)
                continue
            }

            val fileName = file.name
            val vFile = virtualFileManager.findFileByNioPath(file.toPath())


            val isBinary = Optional
                .ofNullable(vFile)
                .map { obj: VirtualFile -> obj.fileType }
                .map { obj: FileType -> obj.isBinary }
                .orElse(true)

            if (isBinary) {
                checkNotNull(vFile)
                VfsUtil.copy(null, vFile, vDirectory)
            } else {
                var contentFile = psiDirectory.findFile(fileName)
                if (contentFile == null) {
                    contentFile = psiDirectory.createFile(fileName)
                }
                CiteckPsiUtils.setContent(contentFile, Files.readString(file.toPath()).replace("\r\n", "\n"))
            }
        }

        vDirectory.refresh(false, false)

        FileUtil.delete(tmpDir)
    }
}
