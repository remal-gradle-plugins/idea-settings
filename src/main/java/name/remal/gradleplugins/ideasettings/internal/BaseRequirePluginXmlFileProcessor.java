package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.XslUtils.transformXmlProvider;

import org.gradle.api.XmlProvider;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
interface BaseRequirePluginXmlFileProcessor extends SpecificIdeaXmlFileProcessor {

    String getPluginId();

    @Override
    default String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

    @Override
    default void execute(XmlProvider xmlProvider) {
        transformXmlProvider(xmlProvider, "add-required-plugin.xsl", transformer -> {
            transformer.setParameter("plugin-id", getPluginId());
        });
    }

}
