package name.remal.gradleplugins.ideasettings;

import name.remal.gradleplugins.ideasettings.internal.IdeaXmlFileSettingsAction;
import org.gradle.api.Action;
import org.gradle.api.XmlProvider;

public interface IdeaXmlFileProcessor extends Action<XmlProvider>, IdeaXmlFileSettingsAction {
}
