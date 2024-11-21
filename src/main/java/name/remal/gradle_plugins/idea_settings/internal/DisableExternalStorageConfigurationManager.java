package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import java.net.URI;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class DisableExternalStorageConfigurationManager extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "misc.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("disable-external-storage-configuration-manager.xsl").toURI();
    }

}
