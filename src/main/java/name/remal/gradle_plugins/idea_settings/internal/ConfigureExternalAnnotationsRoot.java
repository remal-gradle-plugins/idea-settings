package name.remal.gradle_plugins.idea_settings.internal;

import static java.util.Objects.requireNonNull;
import static name.remal.gradle_plugins.toolkit.ObjectUtils.isNotEmpty;
import static name.remal.gradle_plugins.toolkit.PathUtils.normalizePath;
import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import com.google.common.annotations.VisibleForTesting;
import java.io.File;
import java.net.URI;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import org.gradle.internal.os.OperatingSystem;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class ConfigureExternalAnnotationsRoot extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "libraries/*.xml";
    }

    @Override
    public boolean isEnabled() {
        var externalAnnotationsRootDir = getIdeaSettings().getExternalAnnotationsRootDir();
        return externalAnnotationsRootDir != null
            && "file".equals(externalAnnotationsRootDir.toUri().getScheme());
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("configure-external-annotations-root.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        var externalAnnotationsRootDir = requireNonNull(getIdeaSettings().getExternalAnnotationsRootDir());
        var externalAnnotationsRootUri = externalAnnotationsRootDir.toUri();
        String externalAnnotationsRoot = getPathFromUri(externalAnnotationsRootUri);

        var userHomePathString = System.getProperty("user.home");
        if (isNotEmpty(userHomePathString)) {
            var userHomePath = normalizePath(new File(userHomePathString).toPath());
            var userHome = getPathFromUri(userHomePath.toUri());
            if (doesFileUriStartWith(externalAnnotationsRoot, userHome + '/')) {
                externalAnnotationsRoot = externalAnnotationsRootUri.getScheme()
                    + "://$USER_HOME$"
                    + externalAnnotationsRoot.substring(userHome.length());
            }
        }

        transformer.setParameter("external-annotations-root", externalAnnotationsRoot);
    }

    private static String getPathFromUri(URI uri) {
        String string = uri.getPath();
        while (string.endsWith("/")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    @VisibleForTesting
    static boolean doesFileUriStartWith(String uriString, String uriPrefix) {
        if (uriString.startsWith(uriPrefix)) {
            return true;
        }

        if (OperatingSystem.current().isWindows()) {
            if (uriString.toLowerCase().startsWith(uriPrefix.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
