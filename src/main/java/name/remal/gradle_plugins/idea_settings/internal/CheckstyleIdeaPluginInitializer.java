package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import java.net.URI;
import lombok.SneakyThrows;
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
