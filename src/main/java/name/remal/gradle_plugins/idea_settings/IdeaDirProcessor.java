package name.remal.gradle_plugins.idea_settings;

import java.nio.file.Path;
import name.remal.gradle_plugins.idea_settings.internal.IdeaXmlFileSettingsAction;
import org.gradle.api.Action;

public interface IdeaDirProcessor extends IdeaXmlFileSettingsAction, Action<Path> {
}
