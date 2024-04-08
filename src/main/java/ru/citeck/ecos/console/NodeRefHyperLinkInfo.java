package ru.citeck.ecos.console;

import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.settings.EcosServer;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NodeRefHyperLinkInfo implements HyperlinkInfo {


    private record UrlPopupItem(String title, String url) {
        @Override
        public String toString() {
            return title;
        }
    }

    private final String nodeRef;

    public NodeRefHyperLinkInfo(String nodeRef) {
        this.nodeRef = nodeRef;
    }

    @Override
    public void navigate(@NotNull Project project) {
        EcosServer.doWithServer(project, this::navigate);
    }

    private void navigate(EcosServer ecosServer) {
        String host = ecosServer.getHost();
        JBPopupFactory
                .getInstance()
                .createPopupChooserBuilder(List.of(
                        new UrlPopupItem(
                                "Node browser",
                                host + "/share/page/console/admin-console/node-browser#state=panel" + URLEncoder.encode(
                                        "=view&nodeRef=" + nodeRef + "&search=" + nodeRef + "&lang=noderef&store=workspace%3A%2F%2FSpacesStore",
                                        StandardCharsets.UTF_8
                                )
                        ),
                        new UrlPopupItem("Card details", host + "/share/page/card-details?&nodeRef=" + nodeRef),
                        new UrlPopupItem("Card details (old)", host + "/share/page/card-details?&forceOld=true&nodeRef=" + nodeRef)
                ))
                .setTitle("Browse Node In:")
                .setItemChosenCallback(urlPopupItem -> BrowserUtil.browse(urlPopupItem.url))
                .setRequestFocus(true)
                .createPopup()
                .show(RelativePoint.fromScreen(MouseInfo.getPointerInfo().getLocation()));
    }

}
