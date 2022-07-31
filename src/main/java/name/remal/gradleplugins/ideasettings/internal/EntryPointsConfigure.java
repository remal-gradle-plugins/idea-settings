package name.remal.gradleplugins.ideasettings.internal;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toSet;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.ensureJdomElement;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.parseJdomDocument;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.replaceXmlProviderContentWithJdom;
import static name.remal.gradleplugins.toolkit.PredicateUtils.not;

import java.util.Objects;
import lombok.val;
import name.remal.gradle_plugins.api.AutoService;
import name.remal.gradleplugins.toolkit.ObjectUtils;
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
        val document = parseJdomDocument(xmlProvider);

        val entryPointsManager = ensureJdomElement(document.getRootElement(), "component", singletonMap(
            "name", "EntryPointsManager"
        ));

        val list = ensureJdomElement(entryPointsManager, "list");

        val existingEntryPoints = list.getChildren("item").stream()
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
                val item = new Element("item");
                item.setAttribute("index", "");
                item.setAttribute("class", "java.lang.String");
                item.setAttribute("itemvalue", entryPoint);
                list.addContent(item);
            });

        replaceXmlProviderContentWithJdom(xmlProvider, document);
    }

}
