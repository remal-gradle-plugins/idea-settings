package name.remal.gradle_plugins.idea_settings.internal;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
//@AutoService(SpecificIdeaXmlFileInitializer.class)
public class ZzzzSaveActionsPluginConfigurationInitializer
    extends AbstractProjectNodeSpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "saveactions_settings.xml";
    }

    @Override
    public boolean isEnabled() {
        return isConfigured(getIdeaSettings().getRunOnSave());
    }

}
