package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.idea_settings.internal.XsltUtils.transformXmlProvider;

import name.remal.gradle_plugins.idea_settings.IdeaXmlFileProcessor;
import org.gradle.api.XmlProvider;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
abstract class AbstractXsltIdeaXmlFileProcessor
    extends AbstractXsltIdeaXmlFileAction
    implements IdeaXmlFileProcessor {

    @Override
    public final void execute(XmlProvider xmlProvider) {
        transformXmlProvider(xmlProvider, getTemplateUri(), this::configureTransformer);
    }

}
