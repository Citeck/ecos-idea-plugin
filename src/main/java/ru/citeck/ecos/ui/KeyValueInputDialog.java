package ru.citeck.ecos.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class KeyValueInputDialog extends DialogWrapper {

    private final JBTable table;

    public KeyValueInputDialog(Map<String, String> map, String keyColumnName, String valueColumnName) {
        super(true);
        table = new JBTable(new KeyValueTableModel(map, keyColumnName, valueColumnName));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return ToolbarDecorator
                .createDecorator(table)
                .setMinimumSize(new Dimension(600, 800))
                .createPanel();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return table;
    }

}
