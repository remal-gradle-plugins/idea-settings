package name.remal.gradleplugins.ideasettings.internal;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static name.remal.gradleplugins.ideasettings.internal.CommandLineUtils.createCommandLine;
import static name.remal.gradleplugins.ideasettings.internal.CommandLineUtils.parseCommandLine;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.detachJdomElement;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.ensureJdomElement;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.parseJdomDocument;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.replaceXmlProviderContentWithJdom;
import static name.remal.gradleplugins.toolkit.PredicateUtils.not;
import static name.remal.gradleplugins.toolkit.PredicateUtils.startsWithString;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
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
import lombok.val;
import name.remal.gradle_plugins.api.AutoService;
import name.remal.gradleplugins.toolkit.ObjectUtils;
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
        val document = parseJdomDocument(xmlProvider);

        getRunConfigurations(document, "Application", "Application")
            .forEach(this::processJavaApplicationRunConfiguration);

        getRunConfigurations(document, "SpringBootApplicationConfigurationType", "Spring Boot")
            .forEach(this::processSpringBootApplicationRunConfiguration);

        replaceXmlProviderContentWithJdom(xmlProvider, document);
    }

    private void processJavaApplicationRunConfiguration(Element configuration) {
        processShortenCommandLine(
            () -> ensureJdomElement(configuration, "shortenClasspath", emptyMap())
                .setAttribute("name", "ARGS_FILE"),
            () -> detachJdomElement(configuration, "shortenClasspath", emptyMap())
        );

        val jvmParameters = normalizeListOfStrings(
            getIdeaSettings().getRunConfigurations().getJavaApplication().getJvmParameters()
        );
        if (!jvmParameters.isEmpty()) {
            val jvmParametersOption = ensureJdomElement(configuration, "option", singletonMap("name", "VM_PARAMETERS"));
            val existingJvmParameters = parseCommandLine(jvmParametersOption.getAttributeValue("value"));

            val jvmProperties = collectJvmProperties(existingJvmParameters, jvmParameters);
            val mergedJvmParameters = mergeJvmParameters(
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

        val activeProfilesOption = ensureJdomElement(configuration, "option", singletonMap("name", "ACTIVE_PROFILES"));
        val existingActiveProfiles = activeProfilesOption.getAttributeValue("value");

        val jvmParameters = normalizeListOfStrings(
            getIdeaSettings().getRunConfigurations().getJavaApplication().getJvmParameters()
        );
        val jvmParametersOption = ensureJdomElement(configuration, "option", singletonMap("name", "VM_PARAMETERS"));
        val existingJvmParameters = parseCommandLine(jvmParametersOption.getAttributeValue("value"));

        val jvmProperties = collectJvmProperties(existingJvmParameters, jvmParameters);
        val activeProfiles = jvmProperties.remove("spring.profiles.active");
        val activeProfilesToSet = joinSpringProfiles(existingActiveProfiles, activeProfiles);
        activeProfilesOption.setAttribute("value", activeProfilesToSet);

        val mergedJvmParameters = mergeJvmParameters(
            existingJvmParameters,
            jvmParameters,
            jvmProperties
        );
        jvmParametersOption.setAttribute("value", createCommandLine(mergedJvmParameters));
    }


    private void processShortenCommandLine(Runnable onEnabled, Runnable onDisabled) {
        val shortenCommandLine = getIdeaSettings().getRunConfigurations().getJavaApplication().getShortenCommandLine();
        if (TRUE.equals(shortenCommandLine)) {
            onEnabled.run();
        } else if (FALSE.equals(shortenCommandLine)) {
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
                val delimPos = param.indexOf('=');
                if (delimPos > 0) {
                    val key = param.substring(0, delimPos);
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

        val splitter = Splitter.on(',');
        return stream(values)
            .filter(Objects::nonNull)
            .map(splitter::splitToList)
            .flatMap(Collection::stream)
            .map(String::trim)
            .filter(ObjectUtils::isNotEmpty)
            .distinct()
            .collect(joining(","));
    }

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
        val runManager = ensureJdomElement(document.getRootElement(), "component", ImmutableMap.of(
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
