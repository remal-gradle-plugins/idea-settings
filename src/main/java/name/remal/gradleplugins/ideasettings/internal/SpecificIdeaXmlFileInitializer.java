package name.remal.gradleplugins.ideasettings.internal;

import name.remal.gradleplugins.ideasettings.IdeaXmlFileInitializer;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface SpecificIdeaXmlFileInitializer extends IdeaXmlFileInitializer {

    String getRelativeFilePath();

}
