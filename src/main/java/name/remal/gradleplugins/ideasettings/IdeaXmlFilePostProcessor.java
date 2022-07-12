package name.remal.gradleplugins.ideasettings;

import org.gradle.api.Action;
import org.gradle.api.XmlProvider;

public interface IdeaXmlFilePostProcessor extends Action<XmlProvider>, IdeaXmlFileAction {
}
