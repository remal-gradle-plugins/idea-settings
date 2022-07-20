package name.remal.gradleplugins.ideasettings.internal;

import static java.beans.Introspector.getBeanInfo;
import static java.util.Objects.requireNonNull;
import static name.remal.gradleplugins.toolkit.reflection.ReflectionUtils.isNotPublic;
import static name.remal.gradleplugins.toolkit.reflection.ReflectionUtils.unwrapGeneratedSubclass;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import lombok.val;
import name.remal.gradleplugins.ideasettings.IdeaSettings;
import name.remal.gradleplugins.toolkit.PathUtils;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
abstract class AbstractXsltIdeaAction implements IdeaXmlFileSettingsAction {

    private static final String IDEA_DIR_PARAM = "idea-dir";

    @OverridingMethodsMustInvokeSuper
    protected void configureTransformer(Transformer transformer) {
        Optional.ofNullable(ideaDir)
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
    private Path ideaDir;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setIdeaDir(Path ideaDir) {
        this.ideaDir = ideaDir;
    }

    protected final Path getIdeaDir() {
        return requireNonNull(ideaDir, "ideaDir");
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
        return this.getClass().getSimpleName();
    }


    @SneakyThrows
    protected static boolean isConfigured(Object object) {
        for (val prop : getBeanInfo(unwrapGeneratedSubclass(object.getClass())).getPropertyDescriptors()) {
            val readMethod = prop.getReadMethod();
            if (readMethod == null
                || isNotPublic(readMethod)
                || readMethod.getDeclaringClass() == Object.class
            ) {
                continue;
            }

            val value = readMethod.invoke(object);
            if (value instanceof Collection) {
                if (!((Collection<?>) value).isEmpty()) {
                    return true;
                }
            } else if (value != null) {
                return true;
            }
        }

        return false;
    }

}
