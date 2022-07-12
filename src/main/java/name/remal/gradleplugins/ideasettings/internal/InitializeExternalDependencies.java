package name.remal.gradleplugins.ideasettings.internal;

import static name.remal.gradleplugins.ideasettings.internal.IdeaXmlUtils.newDocument;
import static name.remal.gradleplugins.toolkit.xml.DomUtils.appendElement;

import name.remal.gradle_plugins.api.AutoService;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Document;

@Internal
@AutoService(SpecificIdeaXmlFileInitializer.class)
public class InitializeExternalDependencies implements SpecificIdeaXmlFileInitializer {

    @Override
    public String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

    @Override
    public Document get() {
        return appendElement(newDocument(), "project", projectElement -> {
            projectElement.setAttribute("version", "4");

            appendElement(projectElement, "component", componentElement -> {
                componentElement.setAttribute("name", "ExternalDependencies");
            });
        });
    }

}
