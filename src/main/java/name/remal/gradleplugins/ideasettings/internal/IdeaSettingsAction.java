package name.remal.gradleplugins.ideasettings.internal;

import java.nio.file.Path;
import name.remal.gradleplugins.ideasettings.IdeaSettings;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IdeaSettingsAction extends Comparable<IdeaSettingsAction> {

    default void setProject(Project project) {
        // do nothing
    }

    default void setIdeaSettings(IdeaSettings ideaSettings) {
        // do nothing
    }

    default boolean isEnabled() {
        return true;
    }

    default int getOrder() {
        return 0;
    }

    @Override
    default int compareTo(IdeaSettingsAction other) {
        int result = Integer.compare(this.getOrder(), other.getOrder());
        if (result == 0) {
            result = this.getClass().getName().compareTo(other.getClass().getName());
        }
        return result;
    }

}
