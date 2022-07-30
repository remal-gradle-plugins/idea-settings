package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.XsltUtils.generateDocumentWithXslt;

import name.remal.gradleplugins.ideasettings.IdeaXmlFileInitializer;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Document;

@Internal
abstract class AbstractXsltIdeaXmlFileInitializer
    extends AbstractXsltIdeaXmlFileAction
    implements IdeaXmlFileInitializer {

    @Override
    public final Document get() {
        return generateDocumentWithXslt(getTemplateUri(), this::configureTransformer);
    }

}
