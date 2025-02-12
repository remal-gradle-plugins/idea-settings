package name.remal.gradle_plugins.idea_settings.internal;

import static java.beans.Introspector.getBeanInfo;
import static java.util.Objects.requireNonNull;
import static name.remal.gradle_plugins.toolkit.reflection.ReflectionUtils.isNotPublic;
import static name.remal.gradle_plugins.toolkit.reflection.ReflectionUtils.unwrapGeneratedSubclass;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.nio.file.Path;
import java.util.Collection;
import javax.annotation.Nullable;
import lombok.SneakyThrows;
import name.remal.gradle_plugins.idea_settings.IdeaSettings;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
abstract class AbstractIdeaAction implements IdeaXmlFileSettingsAction {

    @Nullable
    private Project project;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setProject(Project project) {
        this.project = project;
    }

    @Nullable
    protected final Project getProjectOrNull() {
        return project;
    }

    protected final Project getProject() {
        return requireNonNull(getProjectOrNull(), "project");
    }


    @Nullable
    private Path ideaDir;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setIdeaDir(Path ideaDir) {
        this.ideaDir = ideaDir;
    }

    @Nullable
    protected final Path getIdeaDirOrNull() {
        return ideaDir;
    }

    protected final Path getIdeaDir() {
        return requireNonNull(getIdeaDirOrNull(), "ideaDir");
    }


    @Nullable
    private IdeaSettings ideaSettings;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void setIdeaSettings(IdeaSettings ideaSettings) {
        this.ideaSettings = ideaSettings;
    }

    @Nullable
    protected final IdeaSettings getIdeaSettingsOrNull() {
        return ideaSettings;
    }

    protected final IdeaSettings getIdeaSettings() {
        return requireNonNull(getIdeaSettingsOrNull(), "ideaSettings");
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


    @SneakyThrows
    protected static boolean isConfigured(Object object) {
        for (var prop : getBeanInfo(unwrapGeneratedSubclass(object.getClass())).getPropertyDescriptors()) {
            var readMethod = prop.getReadMethod();
            if (readMethod == null
                || isNotPublic(readMethod)
                || readMethod.getDeclaringClass() == Object.class
            ) {
                continue;
            }

            var value = readMethod.invoke(object);
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
