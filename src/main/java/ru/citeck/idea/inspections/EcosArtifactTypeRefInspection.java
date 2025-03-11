package ru.citeck.idea.inspections;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.concurrency.AppExecutorUtil;
import icons.Icons;
import org.jetbrains.annotations.NotNull;

import ru.citeck.idea.files.FileTypeService;
import ru.citeck.idea.files.types.citeck.EcosArtifact;
import ru.citeck.idea.files.types.citeck.model.DataType;
import ru.citeck.idea.files.types.citeck.model.Permissions;
import ru.citeck.idea.files.types.citeck.ui.Dashboard;
import ru.citeck.idea.ui.list.ListCellRendererWithIcon;

import java.util.List;

public interface EcosArtifactTypeRefInspection {

    List<Class<? extends EcosArtifact>> TYPE_REF_REQUIRED = List.of(
        Dashboard.class,
        Permissions.class
    );

    default boolean accept(PsiFile psiFile) {
        FileTypeService fileTypeService = FileTypeService.getInstance();
        return TYPE_REF_REQUIRED
            .stream()
            .anyMatch(fileType -> fileTypeService.isInstance(psiFile, fileType));
    }

    @FunctionalInterface
    interface FixTypeRef {
        void fixTypeRef(Project project, ProblemDescriptor descriptor, String eType);
    }

    class QuickFix implements LocalQuickFix {

        @SafeFieldForPreview
        private final FixTypeRef fixer;

        public QuickFix(FixTypeRef fixer) {
            this.fixer = fixer;
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return "Set ECOS Type";
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

            ReadAction
                .nonBlocking(() -> DataType.getDataTypes(project))
                .finishOnUiThread(ModalityState.any(), dataTypes -> JBPopupFactory
                    .getInstance()
                    .createPopupChooserBuilder(dataTypes)
                    .setTitle("ECOS Type:")
                    .setNamerForFiltering(String::toString)
                    .setRequestFocus(true)
                    .setRenderer(new ListCellRendererWithIcon(Icons.CiteckLogo))
                    .setItemChosenCallback(eType -> fixer.fixTypeRef(project, descriptor, eType))
                    .createPopup()
                    .showInCenterOf(WindowManager.getInstance().getFrame(project).getRootPane()))
                .submit(runnable -> AppExecutorUtil.getAppExecutorService().submit(runnable));
        }

    }

}
