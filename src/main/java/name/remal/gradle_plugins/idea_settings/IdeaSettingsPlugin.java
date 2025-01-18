package name.remal.gradle_plugins.idea_settings;

import static java.lang.String.format;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.newBufferedWriter;
import static java.util.Arrays.stream;
import static java.util.Comparator.naturalOrder;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static name.remal.gradle_plugins.idea_settings.IdeaSettings.IDEA_SETTINGS_EXTENSION_NAME;
import static name.remal.gradle_plugins.idea_settings.IdeaSettings.canonizeIdeaSettingsRelativeFilePath;
import static name.remal.gradle_plugins.toolkit.ExtensionContainerUtils.getExtension;
import static name.remal.gradle_plugins.toolkit.GradleUtils.onGradleBuildFinished;
import static name.remal.gradle_plugins.toolkit.PathUtils.createParentDirectories;
import static name.remal.gradle_plugins.toolkit.PathUtils.normalizePath;
import static name.remal.gradle_plugins.toolkit.ProjectUtils.afterEvaluateOrNow;
import static name.remal.gradle_plugins.toolkit.ProjectUtils.getTopLevelDirOf;
import static name.remal.gradle_plugins.toolkit.ProjectUtils.isBuildSrcProject;
import static name.remal.gradle_plugins.toolkit.SneakyThrowUtils.sneakyThrows;
import static name.remal.gradle_plugins.toolkit.git.GitUtils.findGitRepositoryRootFor;
import static name.remal.gradle_plugins.toolkit.reflection.MethodsInvoker.invokeMethod;
import static name.remal.gradle_plugins.toolkit.xml.XmlProviderImpl.newXmlProviderForFile;
import static name.remal.gradle_plugins.toolkit.xml.XmlUtils.compactXmlString;
import static name.remal.gradle_plugins.toolkit.xml.XmlUtils.prettyXmlString;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.CustomLog;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.idea_settings.internal.IdeaSettingsAction;
import name.remal.gradle_plugins.idea_settings.internal.IdeaXmlFileSettingsAction;
import name.remal.gradle_plugins.toolkit.EditorConfig;
import name.remal.gradle_plugins.toolkit.PluginDescription;
import name.remal.gradle_plugins.toolkit.SneakyThrowUtils.SneakyThrowsAction;
import name.remal.gradle_plugins.toolkit.xml.XmlFormat;
import name.remal.gradle_plugins.toolkit.xml.XmlProviderImpl;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.XmlProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.gradle.plugins.ide.idea.model.IdeaModel;
import org.intellij.lang.annotations.Language;
import org.w3c.dom.Document;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;

@CustomLog
public class IdeaSettingsPlugin implements Plugin<Project> {

    @Override
    @SneakyThrows
    public void apply(Project project) {
        if (project.getParent() != null) {
            throw new GradleException("name.remal.idea-settings plugin can be applied for root project only");
        }

        var ideaSettings = project.getExtensions().create(IDEA_SETTINGS_EXTENSION_NAME, IdeaSettings.class, project);

        afterEvaluateOrNow(project, __ -> configure(project, ideaSettings));
    }

    @SuppressWarnings("Slf4jFormatShouldBeConst")
    private static void configure(Project project, IdeaSettings ideaSettings) {
        if (!ideaSettings.isExplicitlyEnabled()) {
            var parentGradle = project.getGradle().getParent();
            if (parentGradle != null) {
                if (isBuildSrcProject(project)) {
                    logger.debug(
                        "Skipping logic of {}, as the current Gradle build is a `buildSrc` and included into "
                            + "other build ({}). You can explicitly enable the logic by adding "
                            + "`ideaSettings.explicitlyEnabled = true` to the build script.",
                        new PluginDescription(IdeaSettingsPlugin.class),
                        parentGradle.getRootProject().getProjectDir()
                    );
                } else {
                    logger.warn(
                        "Skipping logic of {}, as the current Gradle build is included into other build ({}). You can "
                            + "explicitly enable the logic by adding `ideaSettings.explicitlyEnabled = true` "
                            + "to the build script.",
                        new PluginDescription(IdeaSettingsPlugin.class),
                        parentGradle.getRootProject().getProjectDir()
                    );
                }
                ideaSettings.setEnabled(false);
                return;
            }
        }


        project.getPluginManager().apply("idea");
        project.getPluginManager().apply("org.jetbrains.gradle.plugin.idea-ext");
        onGradleBuildFinished(project.getGradle(), __ -> {
            var layoutFile = project.getRootProject().file("layout.json");
            logger.debug("Deleting IDEA layout file: {}", layoutFile);
            try {
                delete(layoutFile.toPath());
            } catch (NoSuchFileException ignore) {
                // do nothing
            } catch (Exception e) {
                logger.warn(format("Failed to delete IDEA layout file: %s: %s", layoutFile, e), e);
            }
        });

        var ideaModel = project.getExtensions().getByType(IdeaModel.class);
        var ideaProject = requireNonNull(ideaModel.getProject(), "ideaModel.project");
        var ideaExt = getExtension(ideaProject, "settings");

        delegateBuildToGradle(ideaExt);


        if (!ideaSettings.isExplicitlyEnabled()) {
            var topLevelDirPath = getTopLevelDirOf(project);
            var repositoryRootPath = findGitRepositoryRootFor(topLevelDirPath);
            if (repositoryRootPath != null && !topLevelDirPath.equals(repositoryRootPath)) {
                logger.warn(
                    "Skipping configuring IDEA settings, as top level dir ({}) differs from Git repository root ({}). "
                        + "You can explicitly enable the logic by adding `ideaSettings.explicitlyEnabled = true` "
                        + "to the build script.",
                    topLevelDirPath,
                    repositoryRootPath
                );
                ideaSettings.setEnabled(false);
                return;
            }
        }


        var editorConfig = new EditorConfig(project);

        configureEncodings(project, ideaExt, editorConfig);
        processIdeaDir(project, ideaExt, ideaSettings);
        initializeIdeaProjectFiles(project, ideaExt, ideaSettings);
        processIdeaProjectFiles(project, ideaExt, ideaSettings);
    }


    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static void delegateBuildToGradle(Object ideaExt) {
        var delegateActions = getExtension(ideaExt, "delegateActions");
        invokeMethod(delegateActions, "setDelegateBuildRunToGradle",
            Boolean.class, true
        );

        var getTestRunnerMethod = delegateActions.getClass().getMethod("getTestRunner");
        var testRunnerType = (Class<Enum<?>>) getTestRunnerMethod.getReturnType();
        var testRunnerValues = requireNonNull(testRunnerType.getEnumConstants(), "Not a enum: " + testRunnerType);
        var testRunnerString = "GRADLE";
        var testRunner = stream(testRunnerValues)
            .filter(value -> value.name().equals(testRunnerString))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException(testRunnerType + " doesn't have constant: " + testRunnerString)
            );
        delegateActions.getClass().getMethod("setTestRunner", testRunnerType)
            .invoke(delegateActions, testRunner);
    }


    private static void configureEncodings(Project project, Object ideaExt, EditorConfig editorConfig) {
        var encodings = getExtension(ideaExt, "encodings");
        var propertiesEncodings = invokeMethod(encodings, Object.class, "getProperties");

        var minTargetCompatibilityJavaVersion = project.getAllprojects()
            .stream()
            .flatMap(p -> p.getTasks().withType(AbstractCompile.class).stream())
            .map(AbstractCompile::getTargetCompatibility)
            .filter(Objects::nonNull)
            .map(JavaVersion::toVersion)
            .min(naturalOrder())
            .orElse(null);

        final String encoding;
        if (minTargetCompatibilityJavaVersion == null) {
            var propertiesEditorConfig = editorConfig.getPropertiesForFileExtension("properties");
            encoding = normalizeEncoding(propertiesEditorConfig.getOrDefault("charset", "ISO-8859-1"));

        } else if (minTargetCompatibilityJavaVersion.compareTo(JavaVersion.VERSION_1_9) < 0) {
            encoding = "ISO-8859-1";

        } else {
            var propertiesEditorConfig = editorConfig.getPropertiesForFileExtension("properties");
            encoding = normalizeEncoding(propertiesEditorConfig.getOrDefault("charset", "UTF-8"));
        }

        invokeMethod(propertiesEncodings, "setEncoding",
            String.class, encoding
        );
        invokeMethod(propertiesEncodings, "setTransparentNativeToAsciiConversion",
            Boolean.class, !encoding.equalsIgnoreCase("UTF-8")
        );
    }

    private static String normalizeEncoding(String encoding) {
        try {
            var charset = Charset.forName(encoding);
            encoding = charset.name();
        } catch (UnsupportedCharsetException expected) {
            // do nothing
        }

        return encoding.toUpperCase();
    }


    private static void processIdeaDir(
        Project project,
        Object ideaExt,
        IdeaSettings ideaSettings
    ) {
        var ideaDirProcessors = ideaSettings.getIdeaDirProcessors();
        if (ideaDirProcessors.isEmpty()) {
            return;
        }

        invokeMethod(ideaExt, "withIDEADir",
            Action.class, sneakyThrows((SneakyThrowsAction<File>) ideaDir -> {
                var normalizedIdeaDir = normalizePath(ideaDir.toPath());

                ideaDirProcessors.forEach(processor -> {
                    processIdeaDir(
                        project,
                        ideaSettings,
                        normalizedIdeaDir,
                        processor
                    );
                });
            })
        );
    }

    private static void processIdeaDir(
        Project project,
        IdeaSettings ideaSettings,
        Path ideaDir,
        Action<Path> processor
    ) {
        if (!initializeIdeaSettingsAction(processor, ideaDir, project, ideaSettings)) {
            return;
        }

        processor.execute(ideaDir);
    }


    private static void initializeIdeaProjectFiles(
        Project project,
        Object ideaExt,
        IdeaSettings ideaSettings
    ) {
        var settingsXmlFileInitializers = ideaSettings.getXmlFileInitializers();
        if (settingsXmlFileInitializers.isEmpty()) {
            return;
        }

        invokeMethod(ideaExt, "withIDEADir",
            Action.class, sneakyThrows((SneakyThrowsAction<File>) ideaDir -> {
                var normalizedIdeaDir = normalizePath(ideaDir.toPath());

                settingsXmlFileInitializers.forEach((relativeFilePath, initializer) -> {
                    initializeIdeaProjectFile(
                        project,
                        ideaSettings,
                        normalizedIdeaDir,
                        relativeFilePath,
                        initializer
                    );
                });
            })
        );
    }

    @SneakyThrows
    private static void initializeIdeaProjectFile(
        Project project,
        IdeaSettings ideaSettings,
        Path ideaDir,
        String relativeFilePath,
        Supplier<Document> initializer
    ) {
        relativeFilePath = canonizeIdeaSettingsRelativeFilePath(relativeFilePath);
        var ideaFilePath = ideaDir.resolve(relativeFilePath);
        if (exists(ideaFilePath)) {
            return;
        }

        if (!initializeIdeaSettingsAction(initializer, ideaDir, project, ideaSettings)) {
            return;
        }

        var document = initializer.get();

        var xmlProvider = new XmlProviderImpl(document);
        executePostProcessors(project, ideaSettings, ideaDir, xmlProvider);

        var xmlFormat = XML_FORMAT;
        var prettyXml = prettyXmlString(xmlProvider.asString().toString(), xmlFormat);
        createParentDirectories(ideaFilePath);
        try (var writer = newBufferedWriter(ideaFilePath, xmlFormat.getCharset())) {
            writer.write(prettyXml);
        }
    }


    private static void processIdeaProjectFiles(
        Project project,
        Object ideaExt,
        IdeaSettings ideaSettings
    ) {
        var settingsXmlFilesProcessors = ideaSettings.getXmlFilesProcessors();
        if (settingsXmlFilesProcessors.isEmpty()) {
            return;
        }

        invokeMethod(ideaExt, "withIDEADir",
            Action.class, sneakyThrows((SneakyThrowsAction<File>) ideaDir -> {
                var normalizedIdeaDir = normalizePath(ideaDir.toPath());

                settingsXmlFilesProcessors.forEach((relativeFilePath, processors) -> {
                    processIdeaProjectFiles(
                        project,
                        ideaSettings,
                        normalizedIdeaDir,
                        relativeFilePath,
                        processors
                    );
                });
            })
        );
    }

    @SneakyThrows
    private static void processIdeaProjectFiles(
        Project project,
        IdeaSettings ideaSettings,
        Path ideaDir,
        String relativeFilePath,
        List<Action<XmlProvider>> processors
    ) {
        var canonizedRelativeFilePath = canonizeIdeaSettingsRelativeFilePath(relativeFilePath);
        project.files(ideaDir).getAsFileTree()
            .matching(it -> it.include(canonizedRelativeFilePath))
            .visit(details -> {
                if (details.isDirectory()) {
                    return;
                }

                var ideaFile = normalizePath(details.getFile().toPath());
                processIdeaProjectFile(
                    project,
                    ideaSettings,
                    ideaDir,
                    ideaFile,
                    processors
                );
            });
    }

    @SneakyThrows
    private static void processIdeaProjectFile(
        Project project,
        IdeaSettings ideaSettings,
        Path ideaDir,
        Path ideaFile,
        List<Action<XmlProvider>> processors
    ) {
        if (!exists(ideaFile)) {
            return;
        }

        processors = processors.stream()
            .filter(processor ->
                initializeIdeaSettingsAction(processor, ideaDir, project, ideaSettings)
            )
            .collect(toList());
        if (processors.isEmpty()) {
            return;
        }

        var xmlProvider = newXmlProviderForFile(ideaFile);
        var xmlBefore = xmlProvider.asString().toString();

        processors.forEach(processor -> processor.execute(xmlProvider));
        executePostProcessors(project, ideaSettings, ideaDir, xmlProvider);

        var xmlAfter = compactXmlString(xmlProvider.asString().toString());
        if (isDifferentXml(xmlBefore, xmlAfter)) {
            var xmlFormat = XML_FORMAT;
            var prettyXml = prettyXmlString(xmlAfter, xmlFormat);
            try (var writer = newBufferedWriter(ideaFile, xmlFormat.getCharset())) {
                writer.write(prettyXml);
            }
        }
    }


    private static void executePostProcessors(
        Project project,
        IdeaSettings ideaSettings,
        Path ideaDir,
        XmlProvider xmlProvider
    ) {
        ideaSettings.getXmlFilesPostProcessors().stream()
            .filter(postProcessor ->
                initializeIdeaSettingsAction(postProcessor, ideaDir, project, ideaSettings)
            )
            .forEach(postProcessor -> {
                postProcessor.execute(xmlProvider);
            });
    }


    private static final XmlFormat XML_FORMAT = XmlFormat.builder()
        .insertFinalNewline(false)
        .build();

    private static boolean isDifferentXml(@Language("XML") String xmlString1, @Language("XML") String xmlString2) {
        var diff = DiffBuilder.compare(Input.fromString(xmlString1))
            .withTest(Input.fromString(xmlString2))
            .normalizeWhitespace()
            .ignoreWhitespace()
            .ignoreElementContentWhitespace()
            .ignoreComments()
            .build();
        return diff.hasDifferences();
    }


    /**
     * @return {@code true} is the action is enabled, {@code false} is the action is disabled
     */
    private static boolean initializeIdeaSettingsAction(
        Object action,
        Path ideaDir,
        Project project,
        IdeaSettings ideaSettings
    ) {
        if (action instanceof IdeaXmlFileSettingsAction) {
            var typedAction = (IdeaXmlFileSettingsAction) action;
            typedAction.setIdeaDir(ideaDir);
        }
        if (action instanceof IdeaSettingsAction) {
            var typedAction = (IdeaSettingsAction) action;
            typedAction.setProject(project.getRootProject());
            typedAction.setIdeaSettings(ideaSettings);
            return typedAction.isEnabled();
        }

        return true;
    }

}
