package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.IdeaPlugins.IDEA_CHECKSTYLE_IDEA_PLUGIN_ID;

import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class RequireCheckstyleIdeaPlugin
    extends AbstractCheckstyleIIeaSpecificIdeaXmlFileProcessor
    implements BaseRequirePluginXmlFileProcessor {

    @Override
    public String getPluginId() {
        return IDEA_CHECKSTYLE_IDEA_PLUGIN_ID;
    }

}
