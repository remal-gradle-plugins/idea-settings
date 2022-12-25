package name.remal.gradle_plugins.idea_settings.internal;

import java.net.URI;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
abstract class AbstractXsltIdeaXmlFileAction extends AbstractXsltIdeaAction {

    protected abstract URI getTemplateUri();

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '['
            + "templateUri=" + getTemplateUri()
            + ']';
    }

}
