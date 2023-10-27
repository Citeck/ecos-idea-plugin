package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.citeck.ecos.codeinsight.forms.Properties;

import java.util.Set;

public abstract class InputComponent extends Component {

    private Boolean optionalWhenDisabled = false;
    private Boolean clearOnHide = false;
    private Properties properties = new Properties();
    private Boolean disableInlineEdit = true;

    public Boolean getOptionalWhenDisabled() {
        return optionalWhenDisabled;
    }

    public Boolean getClearOnHide() {
        return clearOnHide;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    boolean getInput() {
        return true;
    }

    @JsonIgnore
    public abstract Set<String> getSupportedArtifactTypes();

    public Boolean getDisableInlineEdit() {
        return disableInlineEdit;
    }

    public void setDisableInlineEdit(Boolean disableInlineEdit) {
        this.disableInlineEdit = disableInlineEdit;
    }

}
