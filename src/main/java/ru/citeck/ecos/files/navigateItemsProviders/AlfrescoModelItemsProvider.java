package ru.citeck.ecos.files.navigateItemsProviders;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.Nullable;
import ru.citeck.ecos.ServiceRegistry;
import ru.citeck.ecos.files.NavigateInFileItem;
import ru.citeck.ecos.files.NavigateInFileItemsProvider;
import ru.citeck.ecos.files.types.AlfrescoModel;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AlfrescoModelItemsProvider implements NavigateInFileItemsProvider {

    private static final Set<String> TAGS = Set.of(
        "constraint",
        "type",
        "property",
        "association",
        "child-association",
        "aspect"
    );

    @Override
    public @Nullable Collection<NavigateInFileItem> getItems(PsiFile psiFile) {

        if (!ServiceRegistry.getFileTypeService().isInstance(psiFile, AlfrescoModel.class)) {
            return null;
        }

        XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
        if (rootTag == null) {
            return null;
        }

        return PsiTreeUtil
            .findChildrenOfType(psiFile, XmlTag.class)
            .stream()
            .filter(xmlTag -> TAGS.contains(xmlTag.getName()) && xmlTag.getAttribute("name") != null)
            .map(xmlTag -> new NavigateInFileItem(xmlTag.getName() + "@" + xmlTag.getAttributeValue("name"), xmlTag))
            .collect(Collectors.toList());

    }

}
