package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.IdeaPlugins.EDITORCONFIG_IDEA_PLUGIN_ID;

import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class RequireEditorConfigPlugin
    extends AbstractCheckstyleIIeaSpecificIdeaXmlFileProcessor
    implements BaseRequirePluginXmlFileProcessor {

    @Override
    public String getPluginId() {
        return EDITORCONFIG_IDEA_PLUGIN_ID;
    }

}
