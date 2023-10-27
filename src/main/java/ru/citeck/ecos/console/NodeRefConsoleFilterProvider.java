package ru.citeck.ecos.console;

import com.intellij.execution.filters.ConsoleFilterProvider;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeRefConsoleFilterProvider implements ConsoleFilterProvider {

    private final Pattern nodeRefPattern = Pattern.compile("workspace://SpacesStore/[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}");

    @Override
    public @NotNull Filter[] getDefaultFilters(@NotNull Project project) {
        return new Filter[]{this::applyFilter};
    }

    public @Nullable Filter.Result applyFilter(@NotNull String line, int entireLength) {
        int offset = entireLength - line.length();
        Matcher matcher = nodeRefPattern.matcher(line);
        if (!matcher.find()) {
            return null;
        }
        return new Filter.Result(
            offset + matcher.start(),
            offset + matcher.end(),
            new NodeRefHyperLinkInfo(matcher.group())
        );
    }

}
