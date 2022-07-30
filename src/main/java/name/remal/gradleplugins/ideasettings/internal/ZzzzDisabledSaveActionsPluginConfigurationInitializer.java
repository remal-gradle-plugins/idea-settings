package name.remal.gradleplugins.ideasettings.internal;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
//@AutoService(SpecificIdeaXmlFileInitializer.class)
public class ZzzzDisabledSaveActionsPluginConfigurationInitializer
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
