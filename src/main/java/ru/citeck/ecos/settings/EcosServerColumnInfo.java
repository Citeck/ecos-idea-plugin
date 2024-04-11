package ru.citeck.ecos.settings;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBTextField;
import java.util.function.Function;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.function.BiConsumer;

final class EcosServerColumnInfo extends ColumnInfo<EcosServer, String> {

    private final Function<EcosServer, String> getter;
    private final BiConsumer<EcosServer, String> setter;

    private TableCellEditor editor = new DefaultCellEditor(new JBTextField());
    private Function<EcosServer, Boolean> cellEditableEvaluator = ecosServer -> true;

    public EcosServerColumnInfo(
            @NlsContexts.ColumnName String name,
            @NotNull Function<EcosServer, String> getter,
            BiConsumer<EcosServer, String> setter
    ) {
        super(name);
        this.getter = getter;
        this.setter = setter;
    }

    public EcosServerColumnInfo withEditor(TableCellEditor editor) {
        this.editor = editor;
        return this;
    }

    public EcosServerColumnInfo withCellEditableEvaluator(Function<EcosServer, Boolean> cellEditableEvaluator) {
        this.cellEditableEvaluator = cellEditableEvaluator;
        return this;
    }

    @Override
    public @NotNull TableCellEditor getEditor(EcosServer ecosServer) {
        return editor;
    }

    @Override
    public @Nullable String valueOf(EcosServer ecosServer) {
        return getter.apply(ecosServer);
    }

    @Override
    public boolean isCellEditable(EcosServer ecosServer) {
        return cellEditableEvaluator.apply(ecosServer);
    }

    @Override
    public void setValue(EcosServer ecosServer, String value) {
        setter.accept(ecosServer, value);
    }

}
