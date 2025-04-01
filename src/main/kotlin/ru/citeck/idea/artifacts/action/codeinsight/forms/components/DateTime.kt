package ru.citeck.idea.artifacts.action.codeinsight.forms.components

class DateTime : PropertyComponent() {

    companion object {
        const val TYPE = "datetime"
    }

    val datePicker = DatePicker()
    val format = "dd.MM.yyyy"
    val enableTime = false
    val enableDate = true

    class DatePicker {
        var minDate: String = ""
        var maxDate: String = ""
    }

    override fun getType(): String {
        return TYPE
    }
}
