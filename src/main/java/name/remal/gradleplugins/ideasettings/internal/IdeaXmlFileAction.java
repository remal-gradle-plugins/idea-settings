package name.remal.gradleplugins.ideasettings.internal;

import java.nio.file.Path;
import name.remal.gradleplugins.ideasettings.IdeaSettings;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IdeaXmlFileAction {

    default void setProject(Project project) {
        // do nothing
    }

    default void setIdeaDirPath(Path ideaDirPath) {
        // do nothing
    }

    default void setIdeaSettings(IdeaSettings ideaSettings) {
        // do nothing
    }

    default boolean isEnabled() {
        return true;
    }

}
