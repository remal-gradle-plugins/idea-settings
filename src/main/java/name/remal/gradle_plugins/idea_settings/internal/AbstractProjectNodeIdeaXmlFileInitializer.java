package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.toolkit.xml.DomUtils.appendElement;
import static name.remal.gradle_plugins.toolkit.xml.XmlUtils.newDocument;

import name.remal.gradle_plugins.idea_settings.IdeaXmlFileInitializer;
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
