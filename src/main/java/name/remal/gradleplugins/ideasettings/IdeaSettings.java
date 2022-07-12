package name.remal.gradleplugins.ideasettings;

import static name.remal.gradleplugins.toolkit.ObjectUtils.doNotInline;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import lombok.Data;
import lombok.val;
import name.remal.gradleplugins.ideasettings.internal.SpecificIdeaXmlFileInitializer;
import name.remal.gradleplugins.ideasettings.internal.SpecificIdeaXmlFileProcessor;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.XmlProvider;
import org.gradle.api.model.ObjectFactory;
import org.w3c.dom.Document;

@Data
public class IdeaSettings {

    public static final String IDEA_SETTINGS_EXTENSION_NAME = doNotInline("ideaSettings");


    private boolean enabled = true;

    private boolean explicitlyEnabled = false;

    public boolean isEnabled() {
        return this.enabled || isExplicitlyEnabled();
    }


    private final Map<String, Supplier<Document>> xmlFileInitializers = new LinkedHashMap<>();

    {
        streamServices(SpecificIdeaXmlFileInitializer.class)
            .forEach(initializer -> setXmlFileInitializer(initializer.getRelativeFilePath(), initializer));
    }

    public void setXmlFileInitializer(String relativeFilePath, Supplier<Document> initializer) {
        relativeFilePath = canonizeIdeaSettingsRelativeFilePath(relativeFilePath);
        val prevInitializer = xmlFileInitializers.putIfAbsent(relativeFilePath, initializer);
        if (prevInitializer != null) {
            throw new IllegalArgumentException("Initializer for IDEA file has already been added: " + relativeFilePath);
        }
    }


    private final Map<String, List<Action<XmlProvider>>> xmlFilesProcessors = new LinkedHashMap<>();

    {
        streamServices(SpecificIdeaXmlFileProcessor.class)
            .forEach(processor -> processXmlFile(processor.getRelativeFilePath(), processor));
    }

    public void processXmlFile(String relativeFilePath, Action<XmlProvider> action) {
        relativeFilePath = canonizeIdeaSettingsRelativeFilePath(relativeFilePath);
        xmlFilesProcessors.computeIfAbsent(relativeFilePath, __ -> new ArrayList<>())
            .add(action);
    }


    private final List<Action<XmlProvider>> xmlFilesPostProcessors = new ArrayList<>();

    {
        streamServices(IdeaXmlFilePostProcessor.class)
            .forEach(this::addXmlFilePostProcessor);
    }

    public void addXmlFilePostProcessor(Action<XmlProvider> postProcessor) {
        this.xmlFilesPostProcessors.add(postProcessor);
    }


    private final IdeaCheckstyleSettings checkstyle;

    public void checkstyle(Action<IdeaCheckstyleSettings> action) {
        action.execute(checkstyle);
    }


    @Inject
    public IdeaSettings(ObjectFactory objectFactory) {
        this.checkstyle = objectFactory.newInstance(IdeaCheckstyleSettings.class);
    }


    static String canonizeIdeaSettingsRelativeFilePath(String relativeFilePath) {
        relativeFilePath = relativeFilePath.replace(File.separatorChar, '/');

        if (relativeFilePath.startsWith("/")) {
            throw new GradleException("Not a relative IDEA file path: " + relativeFilePath);
        } else if (relativeFilePath.contains("/../")) {
            throw new GradleException("Not a normalized IDEA file path (contains '/../'): " + relativeFilePath);
        }

        return relativeFilePath;
    }

    private static <T> Stream<T> streamServices(Class<T> serviceType) {
        return StreamSupport.stream(
            ServiceLoader.load(serviceType, IdeaSettings.class.getClassLoader()).spliterator(),
            false
        );
    }

}
