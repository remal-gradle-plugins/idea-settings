package name.remal.gradle_plugins.idea_settings.internal;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static name.remal.gradle_plugins.toolkit.PathUtils.normalizePath;
import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class CheckstyleIdeaPluginConfigure extends AbstractCheckstyleIdeaPluginProcessor {

    @Override
    public String getRelativeFilePath() {
        return "checkstyle-idea.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("configure-checkstyle-idea.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        transformer.setParameter("checkstyle-version", getVersion());
        transformer.setParameter("treat-errors-as-warnings", String.valueOf(getTreatErrorsAsWarnings()));
        transformer.setParameter("is-bundled-sun-checks", isBundledSunChecksEnabled());
        transformer.setParameter("is-bundled-google-checks", isBundledGoogleChecksEnabled());

        var configFilePathString = getConfigFilePath();
        if (configFilePathString != null) {
            var configFilePath = normalizePath(Paths.get(configFilePathString));
            var rootDirPath = getProjectRootDir();
            var configFileRelativePath = rootDirPath != null && configFilePath.startsWith(rootDirPath)
                ? rootDirPath.relativize(configFilePath)
                : configFilePath;
            var configFileRelativePathString = configFileRelativePath.toString().replace(File.separatorChar, '/');

            final String locationType;
            final String location;
            if (configFileRelativePath.isAbsolute()) {
                locationType = "LOCAL_FILE";
                location = configFileRelativePathString;
            } else {
                locationType = "PROJECT_RELATIVE";
                location = "$PROJECT_DIR$/" + configFileRelativePathString;
            }
            var description = "Project file - " + configFileRelativePathString;
            transformer.setParameter("config-new-id", UUID.randomUUID().toString());
            transformer.setParameter("config-location-type", locationType);
            transformer.setParameter("config-location", location);
            transformer.setParameter("config-description", description);
        }

        transformer.setParameter("thirdparty-classpath", emptyList());
        var thirdPartyClasspathFilePaths = getThirdPartyClasspathFilePaths();
        if (thirdPartyClasspathFilePaths != null) {
            transformer.setParameter("thirdparty-classpath", thirdPartyClasspathFilePaths.stream()
                .map(this::relativizeThirdPartyClasspathPath)
                .collect(toList())
            );
        }
    }

    private String relativizeThirdPartyClasspathPath(Path path) {
        var rootDirPath = getProjectRootDir();
        var resultPath = rootDirPath != null && path.startsWith(rootDirPath)
            ? rootDirPath.relativize(path)
            : path;
        String resultPathString = resultPath.toString().replace(File.separatorChar, '/');
        if (!resultPath.isAbsolute()) {
            resultPathString = "$PROJECT_DIR$/" + resultPathString;
        }
        return resultPathString;
    }

}
