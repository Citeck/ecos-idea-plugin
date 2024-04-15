package ru.citeck.ecos;

import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor;
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.Processor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.index.IndexKey;
import ru.citeck.ecos.index.IndexValue;
import ru.citeck.ecos.utils.EcosVirtualFileUtils;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class EcosSearchEveryWhereContributor implements SearchEverywhereContributor<IndexValue> {

    private static final String MODULE_NAME = "moduleName";
    private final Project project;

    private final ListCellRenderer<IndexValue> cellRenderer = new ColoredListCellRenderer<>() {

        @Override
        protected void customizeCellRenderer(@NotNull JList<? extends IndexValue> list, IndexValue value, int index, boolean selected, boolean hasFocus) {
            setPaintFocusBorder(false);
            setIcon(value.getIcon());
            setFont(list.getFont());
            append(value.getId(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, list.getForeground()), true);

            String moduleName = value.getProperty(MODULE_NAME);
            if (moduleName != null) {
                append(" " + moduleName, SimpleTextAttributes.GRAY_SMALL_ATTRIBUTES);
            }
        }

    };

    private Optional<String> getJarName(IndexValue indexValue) {
        return Optional
                .ofNullable(indexValue.getFile())
                .filter(fileName -> fileName.contains("!"))
                .map(fileName -> fileName.split("!"))
                .stream()
                .flatMap(Arrays::stream)
                .filter(s -> s.toLowerCase().endsWith(".jar"))
                .findFirst()
                .map(EcosVirtualFileUtils::getFileByPath)
                .map(VirtualFile::getName);
    }

    private String getModuleName(IndexValue indexValue) {
        return Optional
                .ofNullable(EcosVirtualFileUtils.getFileByPath(indexValue.getFile()))
                .map(virtualFile -> ModuleUtil.findModuleForFile(virtualFile, project))
                .map(Module::getName)
                .or(() -> getJarName(indexValue))
                .orElse(null);
    }

    @Override
    public boolean isShownInSeparateTab() {
        return true;
    }

    @Override
    public boolean processSelectedItem(@NotNull IndexValue selected, int modifiers, @NotNull String searchText) {
        VirtualFile virtualFile = EcosVirtualFileUtils.getFileByPath(selected.getFile());
        OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile);
        if (selected.getOffset() == 0) {
            descriptor.navigate(true);
        } else {
            Editor editor = FileEditorManager.getInstance(project).openTextEditor(descriptor, true);
            if (editor == null) {
                return false;
            }
            editor.getCaretModel().moveToOffset(selected.getOffset());
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
        }
        return true;
    }

    @Override
    public boolean isMultiSelectionSupported() {
        return true;
    }

    @Override
    public @NotNull String getSearchProviderId() {
        return getClass().getSimpleName();
    }

    @Override
    public @NotNull @Nls String getGroupName() {
        return "ECOS";
    }

    @Override
    public int getSortWeight() {
        return 0;
    }

    @Override
    public boolean showInFindResults() {
        return false;
    }

    @Override
    public void fetchElements(@NotNull String pattern, @NotNull ProgressIndicator progressIndicator, @NotNull Processor<? super IndexValue> consumer) {
        if (DumbService.isDumb(project)) {
            return;
        }
        ApplicationManager
                .getApplication()
                .runReadAction(() -> {
                    MinusculeMatcher matcher = NameUtil.buildMatcher("*" + pattern).build();
                    ServiceRegistry
                            .getIndexesService(project)
                            .stream(IndexKey.SEARCH_EVERYWHERE)
                            .filter(indexValue -> matcher.matches(indexValue.getId()))
                            .limit(100)
                            .peek(indexValue -> indexValue.setProperty(MODULE_NAME, getModuleName(indexValue)))
                            .forEach(consumer::process);
                });
    }

    @Override
    public @NotNull ListCellRenderer<IndexValue> getElementsRenderer() {
        return cellRenderer;
    }

    @Override
    public @Nullable Object getDataForItem(@NotNull IndexValue element, @NotNull String dataId) {
        return null;
    }

    public static class Factory implements SearchEverywhereContributorFactory<IndexValue> {
        @Override
        public @NotNull SearchEverywhereContributor<IndexValue> createContributor(@NotNull AnActionEvent event) {
            return new EcosSearchEveryWhereContributor(event.getProject());
        }
    }

}
