package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileInitializer.class)
public class InitializeExternalDependencies extends AbstractXsltSpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("initialize-with-project.xsl").toURI();
    }

}
