package ru.citeck.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.citeck.idea.artifacts.ArtifactsMetaLoader;

public class CiteckPluginProjectInitializer implements ProjectActivity {

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // Services initialized lazily. To force artifacts loading call getService.
        ApplicationManager.getApplication().getService(ArtifactsMetaLoader.class);
        return null;
    }
}
