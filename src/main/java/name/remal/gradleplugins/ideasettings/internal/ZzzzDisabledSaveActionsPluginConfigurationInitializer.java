package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.toolkit.ResourceUtils.getResourceUrl;

import java.net.URI;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
//@AutoService(SpecificIdeaXmlFileInitializer.class)
public class ZzzzDisabledSaveActionsPluginConfigurationInitializer extends AbstractXsltSpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "saveactions_settings.xml";
    }

    @Override
    public boolean isEnabled() {
        return isConfigured(getIdeaSettings().getRunOnSave());
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("initialize-with-project.xsl").toURI();
    }

}
