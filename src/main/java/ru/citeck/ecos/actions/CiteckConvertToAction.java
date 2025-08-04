package ru.citeck.ecos.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;
import ru.citeck.ecos.commons.json.Json;
import ru.citeck.ecos.commons.json.YamlUtils;
import org.apache.commons.io.FilenameUtils;
import ru.citeck.ecos.utils.EcosPsiUtils;

public class CiteckConvertToAction extends EcosAction {

    static class YamlToJson extends CiteckConvertToAction {
        public YamlToJson() {
            super(YAMLFile.class, "json");
        }
    }

    static class JsonToYaml extends CiteckConvertToAction {
        public JsonToYaml() {
            super(JsonFile.class, "yml");
        }
    }

    private final Class<? extends PsiFile> fromType;
    private final String toExt;

    public CiteckConvertToAction(Class<? extends PsiFile> fromType, String toExt) {
        this.fromType = fromType;
        this.toExt = toExt;
    }

    @Override
    protected void perform(@NotNull AnActionEvent event) {

        PsiFile psiFile = getPsiFile(event);
        if (psiFile == null) {
            return;
        }
        PsiDirectory parent = psiFile.getParent();
        if (parent == null) {
            throw new RuntimeException("File parent is null.");
        }

        JsonNode fileContent;
        try {
            fileContent = new ObjectMapper(new YAMLFactory()).readValue(psiFile.getText(), JsonNode.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("File reading failed", e);
        }

        String targetContent = fromType == JsonFile.class ? YamlUtils.toNonDefaultString(fileContent) :
                Json.getMapper().toPrettyStringNotNull(fileContent);
        String nameWithoutExt = FilenameUtils.getBaseName(psiFile.getName());

        ApplicationManager.getApplication().runWriteAction(() -> {
            CommandProcessor.getInstance().runUndoTransparentAction(() -> {



//                val targetFile = parent.createFile();
                psiFile.setName(nameWithoutExt + "." + toExt);
                try {
                    EcosPsiUtils.setContent(psiFile, targetContent);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });
        });
    }

    @Override
    boolean canPerform(@NotNull AnActionEvent event) {
        return fromType.isInstance(getPsiFile(event));
    }
}
