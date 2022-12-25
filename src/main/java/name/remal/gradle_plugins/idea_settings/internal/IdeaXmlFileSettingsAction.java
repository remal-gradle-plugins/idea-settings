package name.remal.gradle_plugins.idea_settings.internal;

import java.nio.file.Path;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IdeaXmlFileSettingsAction extends IdeaSettingsAction {

    default void setIdeaDir(Path ideaDir) {
        // do nothing
    }

}
