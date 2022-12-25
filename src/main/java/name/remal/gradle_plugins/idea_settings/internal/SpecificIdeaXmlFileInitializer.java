package name.remal.gradle_plugins.idea_settings.internal;

import name.remal.gradle_plugins.idea_settings.IdeaXmlFileInitializer;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SpecificIdeaXmlFileInitializer extends IdeaXmlFileInitializer, SpecificIdeaXmlFile {
}
