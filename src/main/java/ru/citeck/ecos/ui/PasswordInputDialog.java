package ru.citeck.ecos.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PasswordInputDialog extends DialogWrapper {

    private final JBTextField userName;
    private final JBPasswordField password;

    public PasswordInputDialog(@Nullable Project project, String userName, String password) {
        super(project);
        this.userName = new JBTextField(userName);
        this.userName.setEnabled(false);
        this.password = new JBPasswordField();
        this.password.setText(password == null ? "" : password);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        JPanel panel =  FormBuilder
                .createFormBuilder()
                .addLabeledComponent("User name:", userName)
                .addLabeledComponent("Password:", password)
                .getPanel();
        panel.setMinimumSize(new Dimension(400,0));
        return panel;
    }

    public String getPassword() {
        return new String(password.getPassword());
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return password;
    }
}
