package ru.citeck.ecos.actions.codeinsight;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
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
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.types.ecos.ui.Form;
import ru.citeck.ecos.utils.EcosMessages;
import ru.citeck.ecos.utils.JsonPrettyPrinter;

import javax.swing.*;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
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
                        return type instanceof TextNode && !List.of("asyncData").contains(type.textValue());
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
            return;
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
        boolean isForm = ServiceRegistry.getFileTypeService().isInstance(psiFile, Form.class);
        presentation.setVisible(isForm);
    }
}
