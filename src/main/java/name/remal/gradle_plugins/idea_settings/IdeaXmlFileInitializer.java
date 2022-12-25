package name.remal.gradle_plugins.idea_settings;

import java.util.function.Supplier;
import name.remal.gradle_plugins.idea_settings.internal.IdeaXmlFileSettingsAction;
import org.w3c.dom.Document;

public interface IdeaXmlFileInitializer extends IdeaXmlFileSettingsAction, Supplier<Document> {
}
