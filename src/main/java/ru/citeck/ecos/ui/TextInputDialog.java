package ru.citeck.ecos.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class TextInputDialog extends DialogWrapper {

    private final JBTextField textField;

    public TextInputDialog(String title, String value) {
        super(true);
        setTitle(title);
        this.textField = new JBTextField(value);
        this.textField.setMinimumSize(new Dimension(480, 0));
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return textField;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return textField;
    }

    public String getValue() {
        return textField.getText();
    }

}
