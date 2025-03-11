package ru.citeck.ecos.webapp.api.entity;

import ru.citeck.ecos.commons.entity.SimpleEntityRef;

/**
 * This is override of EntityRefFactory interface from ecos-webapp-api.
 * For some reason META-INF/services mechanism is not working in idea plugin.
 */
@SuppressWarnings("unused")
public interface EntityRefFactory {

    Companion Companion = new Companion();

    class Companion {

        public EntityRef getRef(String appName, String sourceId, String localId) {
            return new SimpleEntityRef(appName, sourceId, localId);
        }
    }
}
