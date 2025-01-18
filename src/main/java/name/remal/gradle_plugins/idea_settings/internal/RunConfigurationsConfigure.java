package name.remal.gradle_plugins.idea_settings.internal;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static name.remal.gradle_plugins.idea_settings.internal.CommandLineUtils.createCommandLine;
import static name.remal.gradle_plugins.idea_settings.internal.CommandLineUtils.parseCommandLine;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.detachJdomElement;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.ensureJdomElement;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.parseJdomDocument;
import static name.remal.gradle_plugins.idea_settings.internal.JdomUtils.replaceXmlProviderContentWithJdom;
import static name.remal.gradle_plugins.toolkit.PredicateUtils.not;
import static name.remal.gradle_plugins.toolkit.PredicateUtils.startsWithString;

import com.google.auto.service.AutoService;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import name.remal.gradle_plugins.toolkit.ObjectUtils;
import org.gradle.api.XmlProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * See
 * <a href="https://web.archive.org/web/20161228144344/https://blogs.msdn.microsoft.com/twistylittlepassagesallalike/2011/04/23/everyone-quotes-command-line-arguments-the-wrong-way/">this article</a>
 */
@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class RunConfigurationsConfigure
    extends AbstractIdeaAction
    implements SpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "workspace.xml";
    }

    @Override
    public void execute(XmlProvider xmlProvider) {
        var document = parseJdomDocument(xmlProvider);

        getRunConfigurations(document, "Application", "Application")
            .forEach(this::processJavaApplicationRunConfiguration);

        getRunConfigurations(document, "SpringBootApplicationConfigurationType", "Spring Boot")
            .forEach(this::processSpringBootApplicationRunConfiguration);

        replaceXmlProviderContentWithJdom(xmlProvider, document);
    }

    private void processJavaApplicationRunConfiguration(Element configuration) {
        processShortenCommandLine(
            () -> ensureJdomElement(configuration, "shortenClasspath")
                .setAttribute("name", "ARGS_FILE"),
            () -> detachJdomElement(configuration, "shortenClasspath")
        );

        var jvmParameters = normalizeListOfStrings(
            getIdeaSettings().getRunConfigurations().getJavaApplication().getJvmParameters()
        );
        if (!jvmParameters.isEmpty()) {
            var jvmParametersOption = ensureJdomElement(configuration, "option", singletonMap("name", "VM_PARAMETERS"));
            var existingJvmParameters = parseCommandLine(jvmParametersOption.getAttributeValue("value"));

            var jvmProperties = collectJvmProperties(existingJvmParameters, jvmParameters);
            var mergedJvmParameters = mergeJvmParameters(
                existingJvmParameters,
                jvmParameters,
                jvmProperties
            );
            jvmParametersOption.setAttribute("value", createCommandLine(mergedJvmParameters));
        }
    }

    private void processSpringBootApplicationRunConfiguration(Element configuration) {
        processShortenCommandLine(
            () -> ensureJdomElement(configuration, "option", singletonMap("name", "SHORTEN_COMMAND_LINE"))
                .setAttribute("value", "ARGS_FILE"),
            () -> detachJdomElement(configuration, "option", singletonMap("name", "SHORTEN_COMMAND_LINE"))
        );

        var activeProfilesOption = ensureJdomElement(configuration, "option", singletonMap("name", "ACTIVE_PROFILES"));
        var existingActiveProfiles = activeProfilesOption.getAttributeValue("value");

        var jvmParameters = normalizeListOfStrings(
            getIdeaSettings().getRunConfigurations().getJavaApplication().getJvmParameters()
        );
        var jvmParametersOption = ensureJdomElement(configuration, "option", singletonMap("name", "VM_PARAMETERS"));
        var existingJvmParameters = parseCommandLine(jvmParametersOption.getAttributeValue("value"));

        var jvmProperties = collectJvmProperties(existingJvmParameters, jvmParameters);
        var activeProfiles = jvmProperties.remove("spring.profiles.active");
        var activeProfilesToSet = joinSpringProfiles(existingActiveProfiles, activeProfiles);
        activeProfilesOption.setAttribute("value", activeProfilesToSet);

        var mergedJvmParameters = mergeJvmParameters(
            existingJvmParameters,
            jvmParameters,
            jvmProperties
        );
        jvmParametersOption.setAttribute("value", createCommandLine(mergedJvmParameters));
    }


    private void processShortenCommandLine(Runnable onEnabled, Runnable onDisabled) {
        var shortenCommandLine = getIdeaSettings().getRunConfigurations().getJavaApplication().getShortenCommandLine();
        if (shortenCommandLine == null) {
            // do nothing
        } else if (shortenCommandLine) {
            onEnabled.run();
        } else {
            onDisabled.run();
        }
    }

    private static Map<String, String> collectJvmProperties(
        List<String> existingJvmParameters,
        List<String> jvmParameters
    ) {
        Map<String, String> jvmProperties = new LinkedHashMap<>();
        Stream.of(existingJvmParameters, jvmParameters)
            .flatMap(Collection::stream)
            .filter(startsWithString("-P"))
            .map(param -> param.substring(2))
            .forEach(param -> {
                var delimPos = param.indexOf('=');
                if (delimPos > 0) {
                    var key = param.substring(0, delimPos);
                    String value = param.substring(delimPos + 1);
                    if (key.equals("spring.profiles.active")) {
                        value = joinSpringProfiles(jvmProperties.get(key), value);
                    }
                    jvmProperties.put(key, value);
                }
            });
        return jvmProperties;
    }

    private static String joinSpringProfiles(@Nullable String... values) {
        if (values == null || values.length == 0) {
            return "";
        }

        var splitter = Splitter.on(',');
        return stream(values)
            .filter(Objects::nonNull)
            .map(splitter::splitToList)
            .flatMap(Collection::stream)
            .map(String::trim)
            .filter(ObjectUtils::isNotEmpty)
            .distinct()
            .collect(joining(","));
    }

    @SuppressWarnings("java:S3864")
    private static Collection<String> mergeJvmParameters(
        List<String> existingJvmParameters,
        List<String> jvmParameters,
        Map<String, String> jvmProperties
    ) {
        Collection<String> mergedJvmParameters = existingJvmParameters.stream()
            .filter(not(startsWithString("-P")))
            .collect(toCollection(LinkedHashSet::new));

        Function<String, Consumer<String>> replaceByPrefix = prefix -> param -> {
            if (param.startsWith(prefix)) {
                mergedJvmParameters.removeIf(startsWithString(prefix));
            }
        };
        jvmParameters.stream()
            .filter(not(startsWithString("-P")))
            .peek(replaceByPrefix.apply("-Xms"))
            .peek(replaceByPrefix.apply("-Xmx"))
            .peek(replaceByPrefix.apply("-Xss"))
            .forEach(mergedJvmParameters::add);

        jvmProperties.forEach((key, value) -> mergedJvmParameters.add("-P" + key + '=' + value));

        return mergedJvmParameters;
    }


    private static List<Element> getRunConfigurations(Document document, String type, @Nullable String factoryName) {
        Element runManager = ensureJdomElement(document.getRootElement(), "component", singletonMap(
            "name", "RunManager"
        ));

        return getRunConfigurations(runManager, type, factoryName);
    }

    private static List<Element> getRunConfigurations(Element runManager, String type, @Nullable String factoryName) {
        Map<String, Object> filterAttrs = new LinkedHashMap<>();
        filterAttrs.put("default", true);
        filterAttrs.put("type", type);
        filterAttrs.put("factoryName", factoryName);
        ensureJdomElement(runManager, "configuration", filterAttrs);

        return runManager.getContent(new ElementFilter("configuration")).stream()
            .filter(element -> Objects.equals(type, element.getAttributeValue("type")))
            .filter(element -> Objects.equals(factoryName, element.getAttributeValue("factoryName")))
            .collect(toList());
    }


    private static List<String> normalizeListOfStrings(Collection<? extends CharSequence> list) {
        return list.stream()
            .filter(Objects::nonNull)
            .map(Objects::toString)
            .collect(toCollection(ArrayList::new));
    }

}
