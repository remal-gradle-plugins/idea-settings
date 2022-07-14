package name.remal.gradleplugins.ideasettings;

import name.remal.gradleplugins.ideasettings.internal.IdeaXmlFileAction;
import org.gradle.api.Action;
import org.gradle.api.XmlProvider;

public interface IdeaXmlFilePostProcessor extends Action<XmlProvider>, IdeaXmlFileAction {
}
