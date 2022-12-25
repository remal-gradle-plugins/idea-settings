package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.idea_settings.internal.XsltUtils.transformXmlProvider;

import name.remal.gradle_plugins.idea_settings.IdeaXmlFilePostProcessor;
import org.gradle.api.XmlProvider;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
abstract class AbstractXsltIdeaXmlFilePostProcessor
    extends AbstractXsltIdeaXmlFileAction
    implements IdeaXmlFilePostProcessor {

    @Override
    public final void execute(XmlProvider xmlProvider) {
        transformXmlProvider(xmlProvider, getTemplateUri(), this::configureTransformer);
    }

}
