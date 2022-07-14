package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.XslUtils.transformXmlProvider;

import name.remal.gradleplugins.ideasettings.IdeaXmlFileProcessor;
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
