package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.idea_settings.internal.AllIdeaPlugins.SAVE_ACTIONS_IDEA_PLUGIN_ID;
import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
//@AutoService(SpecificIdeaXmlFileProcessor.class)
public class ZzzzSaveActionsPluginRequire extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

    @Override
    public boolean isEnabled() {
        return isConfigured(getIdeaSettings().getRunOnSave());
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("required-plugins-add.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        transformer.setParameter("plugin-ids", SAVE_ACTIONS_IDEA_PLUGIN_ID);
    }

}
