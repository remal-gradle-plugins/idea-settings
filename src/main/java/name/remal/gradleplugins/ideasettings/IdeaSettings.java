package name.remal.gradleplugins.ideasettings;

import static java.util.Collections.emptyMap;
import static name.remal.gradleplugins.toolkit.ObjectUtils.doNotInline;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import name.remal.gradleplugins.ideasettings.internal.IdeaSettingsAction;
import name.remal.gradleplugins.ideasettings.internal.SpecificIdeaXmlFileInitializer;
import name.remal.gradleplugins.ideasettings.internal.SpecificIdeaXmlFileProcessor;
import name.remal.gradleplugins.ideasettings.internal.XsltFileIdeaXmlFileInitializer;
import name.remal.gradleplugins.ideasettings.internal.XsltFileIdeaXmlFileProcessor;
import name.remal.gradleplugins.ideasettings.internal.XsltIdeaXmlFilePostProcessor;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.XmlProvider;
import org.w3c.dom.Document;

@Data
public class IdeaSettings {

    public static final String IDEA_SETTINGS_EXTENSION_NAME = doNotInline("ideaSettings");


    private boolean enabled = true;

    private boolean explicitlyEnabled = false;

    public boolean isEnabled() {
        return this.enabled || isExplicitlyEnabled();
    }


    private final Set<String> requiredPlugins = new TreeSet<>();

    public void setRequiredPlugins(Iterable<? extends CharSequence> requiredPlugins) {
        setStringsCollectionFromIterable(this.requiredPlugins, requiredPlugins);
    }


    private final IdeaRunOnSaveSettings runOnSave;

    public void runOnSave(Action<IdeaRunOnSaveSettings> action) {
        action.execute(runOnSave);
    }


    private final IdeaCheckstyleSettings checkstyle;

    public void checkstyle(Action<IdeaCheckstyleSettings> action) {
        action.execute(checkstyle);
    }


    private final IdeaDatabaseSettings database;

    public void database(Action<IdeaDatabaseSettings> action) {
        action.execute(database);
    }


    private final List<Action<Path>> ideaDirProcessors = new ArrayList<>();

    {
        streamServices(IdeaDirProcessor.class)
            .forEach(this::addIdeaDirProcessor);
    }

    public void addIdeaDirProcessor(Action<Path> processor) {
        ideaDirProcessors.add(processor);
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

    /**
     * @param xsltTemplateFile Resolves a file path relative to the project directory of this project. See
     *     {@link Project#file(Object)}.
     */
    public void setXmlFileInitializerXslt(
        String relativeFilePath,
        Object xsltTemplateFile,
        Map<String, Object> templateParams
    ) {
        val xsltTemplateUri = project.file(xsltTemplateFile).toURI();
        setXmlFileInitializer(relativeFilePath, new XsltFileIdeaXmlFileInitializer(xsltTemplateUri, templateParams));
    }

    /**
     * See {@link #setXmlFileInitializerXslt(String, Object, Map)}.
     */
    public void setXmlFileInitializerXslt(
        String relativeFilePath,
        Object xsltTemplateFile
    ) {
        setXmlFileInitializerXslt(relativeFilePath, xsltTemplateFile, emptyMap());
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

    /**
     * @param xsltTemplateFile Resolves a file path relative to the project directory of this project. See
     *     {@link Project#file(Object)}.
     */
    public void processXmlFileWithXslt(
        String relativeFilePath,
        Object xsltTemplateFile,
        Map<String, Object> xsltTemplateParams
    ) {
        val xsltTemplateUri = project.file(xsltTemplateFile).toURI();
        processXmlFile(relativeFilePath, new XsltFileIdeaXmlFileProcessor(xsltTemplateUri, xsltTemplateParams));
    }

    /**
     * See {@link #processXmlFileWithXslt(String, Object, Map)}.
     */
    public void processXmlFileWithXslt(
        String relativeFilePath,
        Object xsltTemplateFile
    ) {
        processXmlFileWithXslt(relativeFilePath, xsltTemplateFile, emptyMap());
    }


    private final List<Action<XmlProvider>> xmlFilesPostProcessors = new ArrayList<>();

    {
        streamServices(IdeaXmlFilePostProcessor.class)
            .forEach(this::addXmlFilesPostProcessor);
    }

    public void addXmlFilesPostProcessor(Action<XmlProvider> postProcessor) {
        this.xmlFilesPostProcessors.add(postProcessor);
    }

    /**
     * @param xsltTemplateFile Resolves a file path relative to the project directory of this project. See
     *     {@link Project#file(Object)}.
     */
    public void addXmlFilesXsltPostProcessor(
        Object xsltTemplateFile,
        Map<String, Object> templateParams
    ) {
        val xsltTemplateUri = project.file(xsltTemplateFile).toURI();
        addXmlFilesPostProcessor(new XsltIdeaXmlFilePostProcessor(xsltTemplateUri, templateParams));
    }

    /**
     * See {@link #addXmlFilesXsltPostProcessor(Object, Map)}.
     */
    public void addXmlFilesXsltPostProcessor(
        Object xsltTemplateFile
    ) {
        addXmlFilesXsltPostProcessor(xsltTemplateFile, emptyMap());
    }


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Project project;

    @Inject
    public IdeaSettings(Project project) {
        this.project = project;
        this.runOnSave = project.getObjects().newInstance(IdeaRunOnSaveSettings.class);
        this.checkstyle = project.getObjects().newInstance(IdeaCheckstyleSettings.class, project);
        this.database = project.getObjects().newInstance(IdeaDatabaseSettings.class);
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

    static void setStringsCollectionFromIterable(
        Collection<String> collection,
        Iterable<? extends CharSequence> iterable
    ) {
        collection.clear();
        StreamSupport.stream(iterable.spliterator(), false)
            .filter(Objects::nonNull)
            .map(Object::toString)
            .forEach(collection::add);
    }


    private static <T extends IdeaSettingsAction> Stream<T> streamServices(Class<T> serviceType) {
        return StreamSupport.stream(
                ServiceLoader.load(serviceType, serviceType.getClassLoader()).spliterator(),
                false
            )
            .sorted();
    }

}
