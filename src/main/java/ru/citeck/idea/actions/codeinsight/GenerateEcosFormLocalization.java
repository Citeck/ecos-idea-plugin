package ru.citeck.idea.actions.codeinsight;

import ecos.com.fasterxml.jackson210.databind.JsonNode;
import ecos.com.fasterxml.jackson210.databind.ObjectMapper;
import ecos.com.fasterxml.jackson210.databind.node.ObjectNode;
import ecos.com.fasterxml.jackson210.databind.node.TextNode;
import com.intellij.codeInsight.actions.SimpleCodeInsightAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.types.citeck.ui.Form;
import ru.citeck.idea.utils.EcosMessages;
import ru.citeck.idea.utils.JsonPrettyPrinter;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GenerateEcosFormLocalization extends SimpleCodeInsightAction {

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(editor.getDocument().getText(), JsonNode.class);

            Set<String> fields = Stream.of("/i18n/ru", "/i18n/en")
                .map(path -> jsonNode.at(path).fieldNames())
                .flatMap(iterator -> StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL), false)
                )
                .collect(Collectors.toSet());

            List<String> messages = jsonNode
                .findParents("type")
                .stream()
                .filter(node -> {
                    JsonNode type = node.get("type");
                    return type instanceof TextNode && !Objects.equals("asyncData", type.textValue());
                })
                .flatMap(node -> Stream.of(node.get("label"), node.get("description"), node.get("title")))
                .filter(node -> node instanceof TextNode)
                .map(JsonNode::textValue)
                .filter(message -> !(message == null || message.isBlank() || fields.contains(message)))
                .distinct()
                .collect(Collectors.toList());

            JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(messages)
                .setTitle("Select Messages for Localization:")
                .setNamerForFiltering(Object::toString)
                .setRequestFocus(true)
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
                .setItemsChosenCallback(selectedMessages -> addLocalization(selectedMessages, jsonNode, editor))
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane());

        } catch (Exception ex) {
            EcosMessages.error("Error while insert component", "Incorrect file content", project);
        }
    }

    private void addLocalization(Set<? extends String> messages, JsonNode jsonNode, Editor editor) {
        String json;
        try {
            messages.forEach(message -> {
                ((ObjectNode) jsonNode.at("/i18n/ru")).put(message, "");
                ((ObjectNode) jsonNode.at("/i18n/en")).put(message, "");
            });
            json = new ObjectMapper().writer(new JsonPrettyPrinter()).writeValueAsString(jsonNode).replace("\r\n", "\n");
        } catch (Exception ex) {
            EcosMessages.error("Error while insert component", "Incorrect file content", editor.getProject());
            return;
        }
        String finalJson = json;
        ApplicationManager.getApplication().runWriteAction(() ->
            CommandProcessor.getInstance().runUndoTransparentAction(() -> editor.getDocument().setText(finalJson))
        );
    }

    @Override
    protected void update(@NotNull Presentation presentation, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        boolean isForm = FileTypeService.getInstance().isInstance(psiFile, Form.class);
        presentation.setVisible(isForm);
    }
}
