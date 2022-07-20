package name.remal.gradleplugins.ideasettings;

import java.util.function.Supplier;
import name.remal.gradleplugins.ideasettings.internal.IdeaXmlFileSettingsAction;
import org.w3c.dom.Document;

public interface IdeaXmlFileInitializer extends IdeaXmlFileSettingsAction, Supplier<Document> {
}
