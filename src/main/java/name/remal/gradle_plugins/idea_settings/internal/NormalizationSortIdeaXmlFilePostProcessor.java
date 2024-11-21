package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.idea_settings.IdeaXmlFilePostProcessor;
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
        return 1000;
    }

}
