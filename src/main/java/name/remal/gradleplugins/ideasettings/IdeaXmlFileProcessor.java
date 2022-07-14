package name.remal.gradleplugins.ideasettings;

import name.remal.gradleplugins.ideasettings.internal.IdeaXmlFileAction;
import org.gradle.api.Action;
import org.gradle.api.XmlProvider;

public interface IdeaXmlFileProcessor extends Action<XmlProvider>, IdeaXmlFileAction {
}
