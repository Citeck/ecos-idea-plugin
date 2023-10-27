package ru.citeck.ecos.livetemplates.functions.alfresco;

import icons.Icons;
import ru.citeck.ecos.livetemplates.functions.IndexEnumMacro;

import javax.swing.*;

public abstract class AlfrescoEnumMacro extends IndexEnumMacro {
    @Override
    public Icon getIcon() {
        return Icons.AlfrescoLogo;
    }
}
