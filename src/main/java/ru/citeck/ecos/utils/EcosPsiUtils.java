package ru.citeck.ecos.utils;

import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.json.psi.JsonValue;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class EcosPsiUtils {

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
                .collect(Collectors.toMap(JsonProperty::getName, EcosPsiUtils::getValue));
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

}
