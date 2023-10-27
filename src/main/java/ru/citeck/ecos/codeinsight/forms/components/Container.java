package ru.citeck.ecos.codeinsight.forms.components;

import java.util.ArrayList;
import java.util.List;

public abstract class Container extends Component {

    private List<Component> components = new ArrayList<>();

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }


}
