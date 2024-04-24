package ru.citeck.ecos.utils;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.io.Decompressor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class EcosZipContentUtil {

    public static void applyBase64ZipContentToDirectory(@NotNull PsiDirectory psiDirectory, @NotNull String content) throws Exception {

        File tmpDir = FileUtil.createTempDirectory("ecos-content", "tmp");
        tmpDir.deleteOnExit();

        File contentZip = new File(tmpDir, "content.zip");

        FileOutputStream fos = new FileOutputStream(contentZip);
        fos.write(Base64.getDecoder().decode(content));
        fos.close();

        Decompressor decompressor = new Decompressor.Zip(contentZip);
        decompressor.extract(tmpDir);

        FileUtil.delete(contentZip);

        VirtualFile vDirectory = psiDirectory.getVirtualFile();
        VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();

        List<File> files = Optional
                .ofNullable(tmpDir.listFiles())
                .stream()
                .flatMap(Arrays::stream)
                .toList();

        for (File file : files) {

            if (file.isDirectory()) {
                psiDirectory.createSubdirectory(file.getName());
                continue;
            }

            String fileName = file.getName();
            VirtualFile vFile = virtualFileManager.findFileByNioPath(file.toPath());


            boolean isBinary = Optional
                    .ofNullable(vFile)
                    .map(VirtualFile::getFileType)
                    .map(FileType::isBinary)
                    .orElse(true);

            if (isBinary) {
                assert vFile != null;
                VfsUtil.copy(null, vFile, vDirectory);
            } else {
                PsiFile contentFile = psiDirectory.findFile(fileName);
                if (contentFile == null) {
                    contentFile = psiDirectory.createFile(fileName);
                }
                EcosPsiUtils.setContent(contentFile, Files.readString(file.toPath()).replace("\r\n", "\n"));
            }
        }

        vDirectory.refresh(false, false);

        FileUtil.delete(tmpDir);
    }

}
