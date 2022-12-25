package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileInitializer.class)
public class CheckstyleIdeaPluginInitializer extends AbstractXsltSpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "checkstyle-idea.xml";
    }

    @Override
    public boolean isEnabled() {
        return getProject().getAllprojects().stream()
            .anyMatch(project -> project.getPluginManager().hasPlugin("checkstyle"));
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("initialize-checkstyle-idea.xsl").toURI();
    }

}
