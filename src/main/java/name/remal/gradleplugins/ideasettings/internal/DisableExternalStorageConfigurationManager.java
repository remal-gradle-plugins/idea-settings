package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.api.AutoService;
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
