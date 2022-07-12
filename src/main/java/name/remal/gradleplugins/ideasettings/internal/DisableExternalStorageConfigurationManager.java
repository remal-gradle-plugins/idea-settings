package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.XslUtils.transformXmlProvider;

import name.remal.gradle_plugins.api.AutoService;
import org.gradle.api.XmlProvider;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class DisableExternalStorageConfigurationManager implements SpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "misc.xml";
    }

    @Override
    public void execute(XmlProvider xmlProvider) {
        transformXmlProvider(xmlProvider, "disable-external-storage-configuration-manager.xsl");
    }

}
