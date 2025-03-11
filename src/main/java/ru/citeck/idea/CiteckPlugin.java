package ru.citeck.idea;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Service required to allow use Disposer.register(CiteckPlugin.getInstance(), disposable)
 */
@Service(Service.Level.APP)
public final class CiteckPlugin implements Disposable {

    private static final Logger log = Logger.getInstance(CiteckPlugin.class);

    public static String ID = "ru.citeck.idea";

    public static @NotNull Disposable getInstance() {
        return ApplicationManager.getApplication().getService(CiteckPlugin.class);
    }

    @Override
    public void dispose() {
        log.info("Disposing CiteckPlugin");
    }
}
