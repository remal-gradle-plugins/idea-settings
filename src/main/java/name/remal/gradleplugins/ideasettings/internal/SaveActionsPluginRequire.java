package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.AllIdeaPlugins.SAVE_ACTIONS_IDEA_PLUGIN_ID;
import static name.remal.gradleplugins.ideasettings.internal.SaveActionsPluginUtils.isSaveActionsPluginConfigured;
import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class SaveActionsPluginRequire extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

    @Override
    public boolean isEnabled() {
        return isSaveActionsPluginConfigured(getIdeaSettings().getRunOnSave());
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
