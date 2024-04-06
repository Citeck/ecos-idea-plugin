package ru.citeck.ecos;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import ru.citeck.ecos.files.FileTypeService;
import ru.citeck.ecos.index.IndexesService;
import ru.citeck.ecos.index.indexers.qname.QNameService;
import ru.citeck.ecos.rest.EcosRestApiService;
import ru.citeck.ecos.settings.EcosSettingsService;
import ru.citeck.ecos.ui.AlfrescoJsConsoleService;

public class ServiceRegistry {

    public static FileTypeService getFileTypeService() {
        return ApplicationManager.getApplication().getService(FileTypeService.class);
    }

    public static EcosRestApiService getEcosRestApiService() {
        return ApplicationManager.getApplication().getService(EcosRestApiService.class);
    }

    public static IndexesService getIndexesService(Project project) {
        return project.getService(IndexesService.class);
    }

    public static AlfrescoJsConsoleService getAlfrescoJsConsoleService(Project project) {
        return project.getService(AlfrescoJsConsoleService.class);
    }

    public static QNameService getQNameService(Project project) {
        return project.getService(QNameService.class);
    }

    public static EcosSettingsService getEcosSettingsService() {
        return ApplicationManager.getApplication().getService(EcosSettingsService.class);
    }

}
