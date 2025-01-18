package name.remal.gradle_plugins.idea_settings.internal;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toSet;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.ensureJdomElement;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.parseJdomDocument;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.replaceXmlProviderContentWithJdom;
import static name.remal.gradle_plugins.toolkit.PredicateUtils.not;

import com.google.auto.service.AutoService;
import java.util.Objects;
import name.remal.gradle_plugins.toolkit.ObjectUtils;
import org.gradle.api.XmlProvider;
import org.jdom2.Element;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class EntryPointsConfigure
    extends AbstractIdeaAction
    implements SpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "misc.xml";
    }

    @Override
    public void execute(XmlProvider xmlProvider) {
        var document = parseJdomDocument(xmlProvider);

        var entryPointsManager = ensureJdomElement(document.getRootElement(), "component", singletonMap(
            "name", "EntryPointsManager"
        ));

        var list = ensureJdomElement(entryPointsManager, "list");

        var existingEntryPoints = list.getChildren("item").stream()
            .map(it -> it.getAttributeValue("itemvalue"))
            .filter(ObjectUtils::isNotEmpty)
            .collect(toSet());

        getIdeaSettings().getEntryPoints().stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .filter(ObjectUtils::isNotEmpty)
            .filter(not(existingEntryPoints::contains))
            .distinct()
            .sorted()
            .forEach(entryPoint -> {
                var item = new Element("item");
                item.setAttribute("index", "");
                item.setAttribute("class", "java.lang.String");
                item.setAttribute("itemvalue", entryPoint);
                list.addContent(item);
            });

        replaceXmlProviderContentWithJdom(xmlProvider, document);
    }

}
