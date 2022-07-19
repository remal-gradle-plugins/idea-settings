package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
import name.remal.gradleplugins.ideasettings.IdeaXmlFilePostProcessor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(IdeaXmlFilePostProcessor.class)
public class NormalizationSortIdeaXmlFilePostProcessor extends AbstractXsltIdeaXmlFilePostProcessor {

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("normalization-sort.xsl").toURI();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

}
