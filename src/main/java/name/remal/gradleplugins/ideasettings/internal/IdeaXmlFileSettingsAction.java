package name.remal.gradleplugins.ideasettings.internal;

import java.nio.file.Path;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IdeaXmlFileSettingsAction extends IdeaSettingsAction {

    default void setIdeaDir(Path ideaDir) {
        // do nothing
    }

}
