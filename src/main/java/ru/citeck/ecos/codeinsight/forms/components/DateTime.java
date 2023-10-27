package ru.citeck.ecos.codeinsight.forms.components;

public class DateTime extends PropertyComponent {

    public static final String TYPE = "datetime";
    private DatePicker datePicker = new DatePicker();
    private String format = "dd.MM.yyyy";
    private Boolean enableTime = false;
    private Boolean enableDate = true;

    @Override
    public String getType() {
        return TYPE;
    }

    public String getFormat() {
        return format;
    }

    public Boolean getEnableTime() {
        return enableTime;
    }

    public Boolean getEnableDate() {
        return enableDate;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    class DatePicker {
        String minDate = "";
        String maxDate = "";

        public String getMinDate() {
            return minDate;
        }

        public String getMaxDate() {
            return maxDate;
        }
    }
}
