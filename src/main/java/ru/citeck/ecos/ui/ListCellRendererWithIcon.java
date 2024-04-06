package ru.citeck.ecos.ui;

import javax.swing.*;

public class ListCellRendererWithIcon extends DefaultListCellRenderer {

    private final Icon icon;
    private final int iconTextGap;

    public ListCellRendererWithIcon(Icon icon) {
        this.icon = icon;
        this.iconTextGap = 4;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public int getIconTextGap() {
        return iconTextGap;
    }

}
