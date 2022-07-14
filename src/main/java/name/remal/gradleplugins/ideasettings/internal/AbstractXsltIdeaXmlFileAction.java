package name.remal.gradleplugins.ideasettings.internal;

import static java.util.Objects.requireNonNull;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.net.URI;
import java.nio.file.Path;
import java.util.Optional;
import javax.xml.transform.Transformer;
import name.remal.gradleplugins.ideasettings.IdeaSettings;
import name.remal.gradleplugins.toolkit.PathUtils;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
abstract class AbstractXsltIdeaXmlFileAction implements IdeaXmlFileAction {

    private static final String IDEA_DIR_PARAM = "idea-dir";


    protected abstract URI getTemplateUri();

    @OverridingMethodsMustInvokeSuper
    protected void configureTransformer(Transformer transformer) {
        Optional.ofNullable(ideaDirPath)
            .map(PathUtils::normalizePath)
            .map(Object::toString)
            .ifPresent(it -> transformer.setParameter(IDEA_DIR_PARAM, it));
    }


    @Nullable
    private Project project;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setProject(Project project) {
        this.project = project;
    }

    protected final Project getProject() {
        return requireNonNull(project, "project");
    }


    @Nullable
    private Path ideaDirPath;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setIdeaDirPath(Path ideaDirPath) {
        this.ideaDirPath = ideaDirPath;
    }

    protected final Path getIdeaDirPath() {
        return requireNonNull(ideaDirPath, "ideaDirPath");
    }


    @Nullable
    private IdeaSettings ideaSettings;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setIdeaSettings(IdeaSettings ideaSettings) {
        this.ideaSettings = ideaSettings;
    }

    protected final IdeaSettings getIdeaSettings() {
        return requireNonNull(ideaSettings, "ideaSettings");
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '['
            + "templateUri=" + getTemplateUri()
            + ']';
    }

}
