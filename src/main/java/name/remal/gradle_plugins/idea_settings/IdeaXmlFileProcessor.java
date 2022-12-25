package name.remal.gradle_plugins.idea_settings;

import name.remal.gradle_plugins.idea_settings.internal.IdeaXmlFileSettingsAction;
import org.gradle.api.Action;
import org.gradle.api.XmlProvider;

public interface IdeaXmlFileProcessor extends Action<XmlProvider>, IdeaXmlFileSettingsAction {
}
