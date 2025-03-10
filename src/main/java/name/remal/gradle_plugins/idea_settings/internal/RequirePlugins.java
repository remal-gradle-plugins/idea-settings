package name.remal.gradle_plugins.idea_settings.internal;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import java.net.URI;
import java.util.Objects;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class RequirePlugins extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "externalDependencies.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("required-plugins-add.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        var requiredPlugins = getIdeaSettings().getRequiredPlugins().stream()
            .filter(Objects::nonNull)
            .map(Object::toString)
            .map(String::trim)
            .filter(not(String::isEmpty))
            .distinct()
            .collect(toList());
        transformer.setParameter("plugin-ids", requiredPlugins);
    }

}
