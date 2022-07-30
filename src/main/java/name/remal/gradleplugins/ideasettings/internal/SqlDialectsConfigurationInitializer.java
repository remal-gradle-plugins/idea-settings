package name.remal.gradleplugins.ideasettings.internal;

import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileInitializer.class)
public class SqlDialectsConfigurationInitializer extends AbstractProjectNodeSpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "sqldialects.xml";
    }

    @Override
    public boolean isEnabled() {
        return isConfigured(getIdeaSettings().getDatabase());
    }

}
