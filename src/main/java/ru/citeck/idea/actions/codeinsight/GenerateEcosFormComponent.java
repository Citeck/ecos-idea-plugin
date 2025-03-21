package ru.citeck.idea.actions.codeinsight;

import ecos.com.fasterxml.jackson210.databind.JsonNode;
import ecos.com.fasterxml.jackson210.databind.ObjectMapper;
import ecos.com.fasterxml.jackson210.databind.node.ArrayNode;
import com.intellij.codeInsight.actions.SimpleCodeInsightAction;
import com.intellij.ide.actions.QualifiedNameProviderUtil;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import ru.citeck.idea.codeinsight.forms.components.*;
import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.types.citeck.ui.Form;
import ru.citeck.idea.index.IndexKey;
import ru.citeck.idea.index.IndexValue;
import ru.citeck.idea.index.IndexesService;
import ru.citeck.idea.index.indexers.EcosDataTypeIndexer;
import ru.citeck.idea.utils.CiteckPsiUtils;
import ru.citeck.idea.utils.CommonUtils;
import ru.citeck.idea.utils.EcosMessages;
import ru.citeck.idea.utils.JsonPrettyPrinter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GenerateEcosFormComponent extends SimpleCodeInsightAction {

    public static final String COMPONENTS_PATH = "/components";

    private static final Map<String, Supplier<Component>> COMPONENTS = Map.of(
        SelectJournal.TYPE, SelectJournal::new,
        DateTime.TYPE, DateTime::new,
        CheckBox.TYPE, CheckBox::new,
        TextField.TYPE, TextField::new,
        TextArea.TYPE, TextArea::new,
        Columns.TYPE + " (1 column)", () -> new Columns(1),
        Columns.TYPE + " (2 columns)", () -> new Columns(2),
        Panel.TYPE, Panel::new,
        EcosSelect.TYPE, EcosSelect::new
    );

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {

        InsertionPosition insertionPosition = getInsertionPath(editor, psiFile);
        if (insertionPosition == null) {
            return;
        }

        JBPopupFactory
            .getInstance()
            .createPopupChooserBuilder(Arrays.asList(COMPONENTS.keySet().toArray()))
            .setTitle("Select Component Type:")
            .setNamerForFiltering(Object::toString)
            .setRequestFocus(true)
            .setItemChosenCallback(componentType -> onComponentTypeSelected(editor, psiFile, (String) componentType, insertionPosition))
            .createPopup()
            .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane());

    }

    private InsertionPosition getInsertionPath(Editor editor, PsiFile psiFile) {

        int selectionStart = editor.getSelectionModel().getSelectionStart();

        PsiElement selectedElement = psiFile.findElementAt(selectionStart);
        if (selectedElement == null) {
            return null;
        }
        PsiElement selectedProperty = PsiTreeUtil.getParentOfType(selectedElement, JsonProperty.class);
        if (selectedProperty == null) {
            return null;
        }

        String qualifiedName = DumbService.getInstance(psiFile.getProject()).computeWithAlternativeResolveEnabled(() ->
            QualifiedNameProviderUtil.getQualifiedName(selectedProperty));

        String path = "/" + qualifiedName
            .replace("]", "/")
            .replace("[", "/")
            .replace(".", "/")
            .replace("//", "/");

        if (!path.contains(COMPONENTS_PATH)) {
            return null;
        }

        path = path.substring(0, path.lastIndexOf(COMPONENTS_PATH) + COMPONENTS_PATH.length());

        int index = 0;

        if (selectedProperty.getChildren().length == 2 && selectedProperty.getChildren()[1] instanceof JsonArray) {
            index = (int) Arrays.stream(selectedProperty.getChildren()[1].getChildren())
                .map(PsiElement::getTextOffset)
                .filter(offset -> offset < selectionStart)
                .count();
        }

        return new InsertionPosition(path, index);

    }

    private void onComponentTypeSelected(Editor editor, @NotNull PsiFile psiFile, String componentType, InsertionPosition insertionPosition) {

        Component component = COMPONENTS.get(componentType).get();

        if (component instanceof InputComponent) {
            JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(getAttributes(psiFile, editor.getProject(), (InputComponent) component))
                .setTitle("Select Attribute:")
                .setNamerForFiltering(Object::toString)
                .setRequestFocus(true)
                .setItemChosenCallback(attribute -> onAttributeSelected(editor, component, attribute, insertionPosition))
                .createPopup()
                .showInCenterOf(WindowManager.getInstance().getFrame(editor.getProject()).getRootPane());
        } else {
            onAttributeSelected(editor, component, "", insertionPosition);
        }
    }

    private void onAttributeSelected(Editor editor, Component component, String attribute, InsertionPosition insertionPosition) {

        Project project = editor.getProject();

        String json;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(editor.getDocument().getText(), JsonNode.class);

            if (component instanceof InputComponent inputComponent) {
                inputComponent.setKey(attribute.replace(":", "_"));
                inputComponent.getProperties().setAttribute(attribute);
                inputComponent.setLabel(attribute);
            } else {
                Set<String> keys = jsonNode.findValues("key").stream().map(JsonNode::asText).collect(Collectors.toSet());
                String key;
                int i = 0;
                do {
                    key = component.getType() + (++i);
                } while (keys.contains(key));
                component.setKey(key);
                component.setLabel(key);
            }

            ((ArrayNode) jsonNode.at(insertionPosition.path())).insert(
                insertionPosition.index(),
                objectMapper.convertValue(component, JsonNode.class)
            );

            json = objectMapper.writer(new JsonPrettyPrinter()).writeValueAsString(jsonNode).replace("\r\n", "\n");

        } catch (Exception ex) {
            EcosMessages.error("Error while insert component", "Incorrect file content", project);
            return;
        }

        String finalJson = json;
        ApplicationManager.getApplication().runWriteAction(
            () -> CommandProcessor.getInstance().runUndoTransparentAction(
                () -> editor.getDocument().setText(finalJson)
            )
        );


    }

    private List<String> getAttributes(@NotNull PsiFile psiFile, Project project, InputComponent component) {

        IndexesService indexesService = IndexesService.getInstance(project);

        List<String> typeRefAttributes = Optional
            .of(psiFile)
            .map(CommonUtils.filterAndCast(JsonFile.class))
            .map(JsonFile::getTopLevelValue)
            .map(CommonUtils.filterAndCast(JsonObject.class))
            .map(jsonObject -> CiteckPsiUtils.getProperty(jsonObject, "typeRef"))
            .map(typeRef -> typeRef.replace("emodel/type", "emodel/types-repo"))
            .map(typeRef -> IndexesService.getInstance(project)
                .stream(new IndexKey(typeRef, EcosDataTypeIndexer.ATTRIBUTE))
                .map(IndexValue::getId)
                .toList())
            .orElse(null);

        if (typeRefAttributes != null && !typeRefAttributes.isEmpty()) {
            return typeRefAttributes;
        }

        return component
            .getSupportedArtifactTypes()
            .stream()
            .map(IndexKey::new)
            .flatMap(indexesService::stream)
            .map(IndexValue::getId)
            .collect(Collectors.toCollection(ArrayList::new));

    }

    @Override
    protected void update(@NotNull Presentation presentation, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        boolean isForm = FileTypeService.getInstance().isInstance(psiFile, Form.JSON.class);
        presentation.setVisible(isForm && getInsertionPath(editor, psiFile) != null);
    }

    private record InsertionPosition(String path, int index) {
    }

}
