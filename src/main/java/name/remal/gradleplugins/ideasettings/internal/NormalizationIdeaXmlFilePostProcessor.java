package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.XslUtils.transformXmlProvider;

import name.remal.gradle_plugins.api.AutoService;
import name.remal.gradleplugins.ideasettings.IdeaXmlFilePostProcessor;
import org.gradle.api.XmlProvider;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(IdeaXmlFilePostProcessor.class)
public class NormalizationIdeaXmlFilePostProcessor implements IdeaXmlFilePostProcessor {

    @Override
    public void execute(XmlProvider xmlProvider) {
        transformXmlProvider(xmlProvider, "normalize.xsl");
    }

}
