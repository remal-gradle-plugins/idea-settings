package name.remal.gradleplugins.ideasettings.internal;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.parseBoolean;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.parseJdomDocument;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.replaceXmlProviderContentWithJdom;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.val;
import org.gradle.api.XmlProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * See
 * <a href="https://web.archive.org/web/20161228144344/https://blogs.msdn.microsoft.com/twistylittlepassagesallalike/2011/04/23/everyone-quotes-command-line-arguments-the-wrong-way/">this article</a>
 */
@SuppressWarnings("deprecation")
@Internal
//@AutoService(SpecificIdeaXmlFileProcessor.class)
public class ZzzzRunConfigurationsConfigure
    extends AbstractIdeaAction
    implements SpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "workspace.xml";
    }

    @Override
    public void execute(XmlProvider xmlProvider) {
        val document = parseJdomDocument(xmlProvider);

        getRunConfigurations(document, "Application", "Application")
            .forEach(this::processJavaApplicationRunConfiguration);

        getRunConfigurations(document, "SpringBootApplicationConfigurationType", "Spring Boot")
            .forEach(this::processSpringBootApplicationRunConfiguration);

        replaceXmlProviderContentWithJdom(xmlProvider, document);
    }

    private void processJavaApplicationRunConfiguration(Element configuration) {
        val shortenCommandLine = getIdeaSettings().getRunConfigurations().getJavaApplication().getShortenCommandLine();
        if (TRUE.equals(shortenCommandLine)) {
            val element = ensureElement(configuration, "shortenClasspath", emptyMap());
            element.setAttribute("name", "ARGS_FILE");
        } else if (FALSE.equals(shortenCommandLine)) {
            Optional.ofNullable(findElement(configuration, "shortenClasspath", emptyMap()))
                .ifPresent(Element::detach);
        }
    }

    private void processSpringBootApplicationRunConfiguration(Element configuration) {
        val shortenCommandLine = getIdeaSettings().getRunConfigurations().getJavaApplication().getShortenCommandLine();
        if (TRUE.equals(shortenCommandLine)) {
            val element = ensureElement(configuration, "option", singletonMap("name", "SHORTEN_COMMAND_LINE"));
            element.setAttribute("value", "ARGS_FILE");
        } else if (FALSE.equals(shortenCommandLine)) {
            Optional.ofNullable(findElement(configuration, "option", singletonMap("name", "SHORTEN_COMMAND_LINE")))
                .ifPresent(Element::detach);
        }

        val activeProfiles = getIdeaSettings().getRunConfigurations().getSpringBootApplication().getActiveProfiles();
        if (activeProfiles != null) {
            val activeProfilesOption = ensureElement(configuration, "option", singletonMap("name", "ACTIVE_PROFILES"));

            List<String> activeProfilesToSet = new ArrayList<>();
            if (!parseBoolean(configuration.getAttributeValue("default"))) {
                Optional.ofNullable(activeProfilesOption.getAttributeValue("value"))
                    .map(Splitter.on(',')::splitToList)
                    .ifPresent(activeProfilesToSet::addAll);
            }
            activeProfilesToSet.addAll(activeProfiles);

            activeProfilesOption.setAttribute(
                "value",
                activeProfilesToSet.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .distinct()
                    .collect(joining(","))
            );
        }
    }


    private static List<Element> getRunConfigurations(Document document, String type, @Nullable String factoryName) {
        val runManager = ensureElement(document.getRootElement(), "component", ImmutableMap.of(
            "name", "RunManager"
        ));

        return getRunConfigurations(runManager, type, factoryName);
    }

    private static List<Element> getRunConfigurations(Element runManager, String type, @Nullable String factoryName) {
        Map<String, Object> filterAttrs = new LinkedHashMap<>();
        filterAttrs.put("default", true);
        filterAttrs.put("type", type);
        filterAttrs.put("factoryName", factoryName);
        ensureElement(runManager, "configuration", filterAttrs);

        return runManager.getContent(new ElementFilter("configuration")).stream()
            .filter(element -> Objects.equals(type, element.getAttributeValue("type")))
            .filter(element -> Objects.equals(factoryName, element.getAttributeValue("factoryName")))
            .collect(toList());
    }

    private static Element ensureElement(Element parentNode, String elementName, Map<String, Object> attrs) {
        val element = findElement(parentNode, elementName, attrs);
        if (element != null) {
            return element;
        }

        val newElement = new Element(elementName);
        attrs.forEach((attrName, attrValue) -> {
            if (attrValue != null) {
                newElement.setAttribute(attrName, attrValue.toString());
            }
        });
        parentNode.addContent(newElement);
        return newElement;
    }

    @Nullable
    private static Element findElement(Element parentNode, String elementName, Map<String, Object> attrs) {
        Map<String, String> normalizedAttrs = new LinkedHashMap<>();
        for (val entry : attrs.entrySet()) {
            val value = entry.getValue();
            normalizedAttrs.put(entry.getKey(), value != null ? value.toString() : null);
        }

        val candidates = parentNode.getContent(new ElementFilter(elementName));
        for (val candidate : candidates) {
            val matches = normalizedAttrs.entrySet().stream()
                .allMatch(entry -> {
                    val attrValue = candidate.getAttributeValue(entry.getKey());
                    return Objects.equals(attrValue, entry.getValue());
                });
            if (matches) {
                return candidate;
            }
        }

        return null;
    }

}
