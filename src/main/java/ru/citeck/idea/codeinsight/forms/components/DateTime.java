package ru.citeck.idea.codeinsight.forms.components;

import lombok.Getter;

@Getter
public class DateTime extends PropertyComponent {

    public static final String TYPE = "datetime";
    private final DatePicker datePicker = new DatePicker();
    private final String format = "dd.MM.yyyy";
    private final Boolean enableTime = false;
    private final Boolean enableDate = true;

    @Override
    public String getType() {
        return TYPE;
    }

    @Getter
    static class DatePicker {
        String minDate = "";
        String maxDate = "";
    }

}
