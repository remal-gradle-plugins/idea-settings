package name.remal.gradleplugins.ideasettings;

import java.nio.file.Path;
import name.remal.gradleplugins.ideasettings.internal.IdeaXmlFileSettingsAction;
import org.gradle.api.Action;

public interface IdeaDirProcessor extends IdeaXmlFileSettingsAction, Action<Path> {
}
