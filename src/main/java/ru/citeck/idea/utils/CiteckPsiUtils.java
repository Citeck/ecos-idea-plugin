package ru.citeck.idea.utils;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.json.psi.JsonValue;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CiteckPsiUtils {

    public static XmlTag getRootTag(@NotNull PsiFile psiFile) {
        if (!(psiFile instanceof XmlFile)) {
            return null;
        }
        return ((XmlFile) psiFile).getRootTag();
    }

    public static Map<String, String> getProperties(@NotNull JsonObject jsonObject, String... property) {
        Set<String> properties = Set.of(property);
        return jsonObject
            .getPropertyList()
            .stream()
            .filter(jsonProperty -> properties.contains(jsonProperty.getName()))
            .collect(Collectors.toMap(JsonProperty::getName, CiteckPsiUtils::getValue));
    }

    public static String getProperty(@NotNull JsonObject jsonObject, String property) {
        return getValue(jsonObject.findProperty(property));
    }

    public static @NotNull String getValue(@Nullable JsonProperty jsonProperty) {
        if (jsonProperty == null) {
            return "";
        }
        JsonValue jsonValue = jsonProperty.getValue();
        if (jsonValue == null) {
            return "";
        }
        String value = jsonValue.getText();
        if (value == null) {
            return "";
        }
        return JsonPsiUtil.stripQuotes(value);
    }

    public static void setContent(PsiFile psiFile, String content) throws Exception {
        Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
        if (document != null) {
            document.setText(content);
            return;
        }
        psiFile.getVirtualFile().setBinaryContent(content.getBytes(StandardCharsets.UTF_8));
    }
}
