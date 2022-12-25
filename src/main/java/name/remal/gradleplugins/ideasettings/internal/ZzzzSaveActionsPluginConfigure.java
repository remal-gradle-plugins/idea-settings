package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
//@AutoService(SpecificIdeaXmlFileProcessor.class)
public class ZzzzSaveActionsPluginConfigure extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "saveactions_settings.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("zzzz-disabled-run-on-save-configure-plugin.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        val runOnSaveSettings = getIdeaSettings().getRunOnSave();
        transformer.setParameter("reformat-mode", String.valueOf(runOnSaveSettings.getReformatMode()));
        transformer.setParameter("optimize-imports", String.valueOf(runOnSaveSettings.getOptimizeImports()));
        //transformer.setParameter("custom-actions-enabled", runOnSaveSettings.getEnabledCustomPluginActions());
        //transformer.setParameter("custom-actions-disabled", runOnSaveSettings.getDisabledCustomPluginActions());
    }

}
