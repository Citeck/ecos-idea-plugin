package ru.citeck.ecos.ui;

import javax.swing.table.DefaultTableCellRenderer;

public class PasswordCellRenderer
        extends DefaultTableCellRenderer {

    public PasswordCellRenderer() {
        super();
    }

    @Override
    protected void setValue(Object value) {
        int len = value == null? 0: value.toString().length();
        super.setValue("*".repeat(len));
    }

}