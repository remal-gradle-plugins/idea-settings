package name.remal.gradleplugins.ideasettings.internal;

import java.nio.file.Path;
import name.remal.gradleplugins.ideasettings.IdeaSettings;
import org.gradle.api.Project;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IdeaXmlFileAction extends Comparable<IdeaXmlFileAction> {

    default void setProject(Project project) {
        // do nothing
    }

    default void setIdeaDir(Path ideaDir) {
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
    default int compareTo(IdeaXmlFileAction other) {
        int result = Integer.compare(this.getOrder(), other.getOrder());
        if (result == 0) {
            result = this.getClass().getName().compareTo(other.getClass().getName());
        }
        return result;
    }

}
