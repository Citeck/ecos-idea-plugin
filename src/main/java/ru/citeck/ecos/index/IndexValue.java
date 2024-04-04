package ru.citeck.ecos.index;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.xml.XmlTokenImpl;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.io.DataExternalizer;
import icons.Icons;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;

import javax.swing.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@EqualsAndHashCode
public class IndexValue {

    private static final Icon DEFAULT_ICON = Icons.CiteckLogo;
    private static final Map<String, Icon> ICONS = Map.of(
            "alfresco", Icons.AlfrescoLogo
    );
    @Getter
    private final String id;
    @Getter
    private final Integer offset;
    @Getter
    private final String file;
    private Map<String, String> properties;

    public String getProperty(String property) {
        return properties == null ? null : properties.get(property);
    }

    public IndexValue setProperty(String property, String value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(property, value);
        return this;
    }

    public PsiElement getPsiElement(Project project) {
        VirtualFile virtualFile = EcosVirtualFileUtils.getFileByPath(file);
        if (virtualFile == null) {
            return null;
        }
        PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
        if (psiFile == null) {
            return null;
        }
        if (offset == 0) {
            return psiFile;
        }
        PsiElement element = PsiUtil.getElementAtOffset(psiFile, offset);
        if (element instanceof XmlTokenImpl) {
            return element.getParent();
        }
        return element;
    }

    public Icon getIcon() {
        String icon = getProperty("icon");
        if (icon != null) {
            return ICONS.getOrDefault(icon, DEFAULT_ICON);
        }
        return DEFAULT_ICON;
    }

    public static class Externalizer implements DataExternalizer<List<IndexValue>> {

        @Override
        public void save(@NotNull DataOutput out, List<IndexValue> indexValues) throws IOException {
            out.writeInt(indexValues.size());
            for (IndexValue indexValue : indexValues) {
                out.writeUTF(indexValue.id);
                out.writeInt(indexValue.offset);
                out.writeUTF(indexValue.file);
                Map<String, String> properties = indexValue.properties;
                if (properties == null) {
                    out.writeInt(0);
                } else {
                    out.writeInt(properties.size());
                    for (String property : properties.keySet()) {
                        out.writeUTF(property);
                        out.writeUTF(properties.get(property));
                    }
                }
            }
        }

        @Override
        public List<IndexValue> read(@NotNull DataInput in) throws IOException {
            List<IndexValue> indexValues = new ArrayList<>();
            int size = in.readInt();
            for (int counter = 0; counter < size; counter++) {
                IndexValue indexValue = new IndexValue(
                        in.readUTF(),
                        in.readInt(),
                        in.readUTF()
                );
                int propSize = in.readInt();
                for (int i = 0; i < propSize; i++) {
                    indexValue.setProperty(in.readUTF(), in.readUTF());
                }
                indexValues.add(indexValue);
            }
            return indexValues;
        }

    }

}
