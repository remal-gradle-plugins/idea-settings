package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
import name.remal.gradle_plugins.idea_settings.IdeaXmlFilePostProcessor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(IdeaXmlFilePostProcessor.class)
public class NormalizationFixesIdeaXmlFilePostProcessor extends AbstractXsltIdeaXmlFilePostProcessor {

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("normalization-fixes.xsl").toURI();
    }

    @Override
    public int getOrder() {
        return -1000;
    }

}
