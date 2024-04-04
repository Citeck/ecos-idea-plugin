package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Columns extends Component {

    public static final String TYPE = "columns";

    private final List<Column> columns = new ArrayList<>();
    private final Boolean oneColumnInViewMode = true;

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
