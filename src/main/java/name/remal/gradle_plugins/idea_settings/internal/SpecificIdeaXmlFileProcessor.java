package name.remal.gradle_plugins.idea_settings.internal;

import name.remal.gradle_plugins.idea_settings.IdeaXmlFileProcessor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SpecificIdeaXmlFileProcessor extends IdeaXmlFileProcessor, SpecificIdeaXmlFile {
}
