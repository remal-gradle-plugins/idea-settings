package name.remal.gradleplugins.ideasettings;

import java.nio.file.Path;
import org.gradle.api.Project;

interface IdeaXmlFileAction {

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
