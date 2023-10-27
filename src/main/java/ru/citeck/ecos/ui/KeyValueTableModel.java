package ru.citeck.ecos.ui;

import javax.swing.table.DefaultTableModel;
import java.util.Map;

public class KeyValueTableModel extends DefaultTableModel {

    private final Map<String, String> map;

    public KeyValueTableModel(Map<String, String> map, String keyColumnName, String valueColumnName) {
        this.map = map;
        addColumn(keyColumnName);
        addColumn(valueColumnName);
        map.forEach((key, value) -> addRow(new String[]{key, value}));
    }

    public KeyValueTableModel(Map<String, String> map) {
        this(map, "Key", "Value");
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        map.put((String) getValueAt(row, 0), (String) aValue);
        super.setValueAt(aValue, row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }

}
