package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.XslUtils.transformXmlProvider;

import name.remal.gradleplugins.ideasettings.IdeaXmlFilePostProcessor;
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
