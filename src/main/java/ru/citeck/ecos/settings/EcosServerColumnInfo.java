package ru.citeck.ecos.settings;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.Function;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

final class EcosServerColumnInfo extends ColumnInfo<EcosServer, String> {

    private final Function<EcosServer, String> getter;
    private final BiConsumer<EcosServer, String> setter;

    private final Supplier<TableCellEditor> editorSupplier;

    public EcosServerColumnInfo(
            @NlsContexts.ColumnName String name,
            @NotNull Function<EcosServer, String> getter,
            BiConsumer<EcosServer, String> setter,
            Supplier<TableCellEditor> editorSupplier
    ) {
        super(name);
        this.getter = getter;
        this.setter = setter;
        this.editorSupplier = editorSupplier;
    }

    public EcosServerColumnInfo(
            @NlsContexts.ColumnName String name,
            @NotNull Function<EcosServer, String> getter,
            BiConsumer<EcosServer, String> setter
    ) {
        super(name);
        this.getter = getter;
        this.setter = setter;
        this.editorSupplier = () -> new DefaultCellEditor(new JBTextField());
    }

    @Override
    public @NotNull TableCellEditor getEditor(EcosServer ecosServer) {
        return editorSupplier.get();
    }

    @Override
    public @Nullable String valueOf(EcosServer ecosServer) {
        return getter.apply(ecosServer);
    }

    @Override
    public boolean isCellEditable(EcosServer ecosServer) {
        return true;
    }

    @Override
    public void setValue(EcosServer ecosServer, String value) {
        setter.accept(ecosServer, value);
    }

}
