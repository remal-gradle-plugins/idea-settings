package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import lombok.val;
import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class ActionsOnSaveConfigure extends AbstractCheckstyleIdeaPluginProcessor {

    @Override
    public String getRelativeFilePath() {
        return "workspace.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("configure-actions-on-save.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        val runOnSaveSettings = getIdeaSettings().getRunOnSave();
        transformer.setParameter("reformat-mode", String.valueOf(runOnSaveSettings.getReformatMode()));
        transformer.setParameter("optimize-imports", String.valueOf(runOnSaveSettings.getOptimizeImports()));
    }
}
