package ru.citeck.ecos.files.deployers;

import com.intellij.designer.clipboard.SimpleTransferable;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiFile;
import ru.citeck.ecos.settings.EcosServer;

import java.awt.datatransfer.DataFlavor;

public class TomcatFileClipboardDeployer extends TomcatFileDeployer {

    @Override
    public void deploy(EcosServer ecosServer, PsiFile psiFile) throws Exception {
        CopyPasteManager
                .getInstance()
                .setContents(new SimpleTransferable(getDeploymentScript(psiFile), DataFlavor.stringFlavor));
    }

    @Override
    public String getDestinationName(EcosServer ecosServer, PsiFile psiFile) {
        return "Clipboard";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
