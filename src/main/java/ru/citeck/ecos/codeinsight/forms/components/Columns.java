package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Columns extends Component {

    public static final String TYPE = "columns";

    private List<Column> columns = new ArrayList<>();
    private Boolean oneColumnInViewMode = true;

    public Columns(Integer columnsCount) {
        for (int i = 0; i < columnsCount; i++) {
            Column column = new Column();
            column.setIndex(i);
            this.columns.add(column);
        }
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public Boolean getOneColumnInViewMode() {
        return oneColumnInViewMode;
    }

    public List<Column> getColumns() {
        return columns;
    }

    @Override
    @JsonIgnore
    public String getLabel() {
        return super.getLabel();
    }
}
