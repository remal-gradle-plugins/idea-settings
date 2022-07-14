package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
import name.remal.gradleplugins.ideasettings.IdeaXmlFilePostProcessor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(IdeaXmlFilePostProcessor.class)
public class NormalizationIdeaXmlFilePostProcessor extends AbstractXsltIdeaXmlFilePostProcessor {

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("normalize.xsl").toURI();
    }

}
