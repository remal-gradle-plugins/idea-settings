package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradle_plugins.toolkit.xml.DomUtils.appendElement;
import static name.remal.gradle_plugins.toolkit.xml.XmlUtils.newDocument;

import name.remal.gradleplugins.ideasettings.IdeaXmlFileInitializer;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Document;

@Internal
abstract class AbstractProjectNodeIdeaXmlFileInitializer
    extends AbstractIdeaAction
    implements IdeaXmlFileInitializer {

    @Override
    public final Document get() {
        return appendElement(newDocument(), "project", project -> {
            project.setAttribute("version", "4");
        });
    }

}
