package name.remal.gradle_plugins.idea_settings.internal;

import com.google.auto.service.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileInitializer.class)
public class InitializeExternalDependencies extends AbstractProjectNodeSpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

}
