package name.remal.gradle_plugins.idea_settings.internal;

import static java.lang.String.join;
import static java.util.stream.Collectors.toCollection;
import static lombok.AccessLevel.PROTECTED;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.val;
import name.remal.gradle_plugins.idea_settings.IdeaSettings;
import name.remal.gradle_plugins.toolkit.LazyInitializer;
import name.remal.gradle_plugins.toolkit.PathUtils;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.resources.TextResource;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

@Internal
@Getter(PROTECTED)
abstract class AbstractCheckstyleIdeaPluginProcessor extends AbstractXsltSpecificIdeaXmlFileProcessor {

    private boolean enabled = false;

    @Override
    public final boolean isEnabled() {
        return enabled;
    }


    @Nullable
    private Path projectRootDir;

    @Nullable
    private String version;

    @Nullable
    private Boolean treatErrorsAsWarnings;

    @Nullable
    private String configFilePath;

    private boolean bundledSunChecksEnabled;

    private boolean bundledGoogleChecksEnabled;

    @Nullable
    private LazyInitializer<Set<Path>> thirdPartyClasspathFilePaths;

    @Override
    @MustBeInvokedByOverriders
    public void setProject(Project project) {
        super.setProject(project);

        val checkstyleProject = project.getAllprojects().stream()
            .filter(it -> it.getPluginManager().hasPlugin("checkstyle"))
            .findFirst()
            .orElse(null);
        if (checkstyleProject == null) {
            return;
        }

        val checkstyle = checkstyleProject.getExtensions().getByType(CheckstyleExtension.class);

        this.enabled = true;
        this.version = checkstyle.getToolVersion();

        if (this.configFilePath == null) {
            //noinspection ConstantConditions
            Optional.ofNullable(checkstyle.getConfig())
                .map(TextResource::asFile)
                .map(File::getPath)
                .ifPresent(configFilePath ->
                    this.configFilePath = configFilePath
                );
        }

        this.thirdPartyClasspathFilePaths = LazyInitializer.of(() -> {
            val originalCheckstyleConfiguration = checkstyleProject.getConfigurations().getByName("checkstyle");

            val consumableConfigurationName = join(
                "$",
                originalCheckstyleConfiguration.getName(),
                AbstractCheckstyleIdeaPluginProcessor.class.getSimpleName()
            );
            final Configuration consumableConfiguration;
            if (checkstyleProject.getConfigurations().findByName(consumableConfigurationName) == null) {
                consumableConfiguration = checkstyleProject.getConfigurations()
                    .create(consumableConfigurationName, conf -> {
                        conf.extendsFrom(originalCheckstyleConfiguration);
                        conf.setCanBeConsumed(true);
                        conf.setCanBeResolved(true);
                        conf.exclude(ImmutableMap.of(
                            "group", "com.puppycrawl.tools",
                            "module", "checkstyle"
                        ));
                    });
            } else {
                consumableConfiguration = checkstyleProject.getConfigurations().getByName(consumableConfigurationName);
            }

            final Configuration checkstyleConfiguration;
            if (checkstyleProject == project) {
                checkstyleConfiguration = consumableConfiguration;
            } else {
                val dependency = project.getDependencies().project(ImmutableMap.of(
                    "path", checkstyleProject.getPath(),
                    "configuration", consumableConfiguration.getName()
                ));
                checkstyleConfiguration = project.getConfigurations().detachedConfiguration(dependency);
            }

            return getThirdPartyClasspathFilePaths(checkstyleConfiguration);
        });
    }

    private static Set<Path> getThirdPartyClasspathFilePaths(Configuration configuration) {
        return configuration
            .getResolvedConfiguration()
            .getLenientConfiguration()
            .getFiles()
            .stream()
            .filter(File::exists)
            .map(File::toPath)
            .map(PathUtils::normalizePath)
            .collect(toCollection(LinkedHashSet::new));
    }

    @Override
    @MustBeInvokedByOverriders
    public void setIdeaDir(Path ideaDir) {
        super.setIdeaDir(ideaDir);

        this.projectRootDir = ideaDir.getParent();
    }

    @Override
    @MustBeInvokedByOverriders
    public void setIdeaSettings(IdeaSettings ideaSettings) {
        super.setIdeaSettings(ideaSettings);

        this.treatErrorsAsWarnings = ideaSettings.getCheckstyle().getTreatErrorsAsWarnings();

        val checkstyleSettings = ideaSettings.getCheckstyle();
        if (checkstyleSettings.isBundledSunChecksEnabled()) {
            this.bundledSunChecksEnabled = true;
        } else if (checkstyleSettings.isBundledGoogleChecksEnabled()) {
            this.bundledGoogleChecksEnabled = true;
        } else {
            Optional.ofNullable(checkstyleSettings.getConfigFilePath())
                .ifPresent(it -> this.configFilePath = it);
        }
    }

}
