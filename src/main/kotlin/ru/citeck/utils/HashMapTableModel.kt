package ru.citeck.utils

import javax.swing.table.DefaultTableModel

class HashMapTableModel(
    val hashMap: HashMap<String, String>,
    val keyColumnName: String = "Key",
    val valueColumnName: String = "Value"
) : DefaultTableModel() {

    init {
        addColumn(keyColumnName)
        addColumn(valueColumnName)
        hashMap.forEach { entry ->
            addRow(arrayOf(entry.key, entry.value))
        }
    }

    override fun setValueAt(aValue: Any?, row: Int, column: Int) {
        hashMap[getValueAt(row, 0) as String] = aValue as String
        super.setValueAt(aValue, row, column)
    }

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return column != 0
    }
}