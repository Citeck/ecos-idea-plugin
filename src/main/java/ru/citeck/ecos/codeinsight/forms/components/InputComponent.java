package ru.citeck.ecos.codeinsight.forms.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.citeck.ecos.codeinsight.forms.Properties;

import java.util.Set;

@Getter
public abstract class InputComponent extends Component {

    private final Boolean optionalWhenDisabled = false;
    private final Boolean clearOnHide = false;
    private final Properties properties = new Properties();
    @Setter
    private Boolean disableInlineEdit = true;

    @Override
    boolean getInput() {
        return true;
    }

    @JsonIgnore
    public abstract Set<String> getSupportedArtifactTypes();

}
