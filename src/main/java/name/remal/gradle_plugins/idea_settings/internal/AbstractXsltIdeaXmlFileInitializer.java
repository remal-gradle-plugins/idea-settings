package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.idea_settings.internal.XsltUtils.generateDocumentWithXslt;

import name.remal.gradle_plugins.idea_settings.IdeaXmlFileInitializer;
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
