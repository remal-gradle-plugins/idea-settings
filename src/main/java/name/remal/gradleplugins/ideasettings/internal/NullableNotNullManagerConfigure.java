package name.remal.gradleplugins.ideasettings.internal;

import static java.util.Collections.singletonMap;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.ensureJdomElement;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.parseJdomDocument;
import static name.remal.gradleplugins.ideasettings.internal.JdomUtils.replaceXmlProviderContentWithJdom;
import static name.remal.gradleplugins.toolkit.ObjectUtils.isNotEmpty;

import lombok.val;
import name.remal.gradle_plugins.api.AutoService;
import org.gradle.api.XmlProvider;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class NullableNotNullManagerConfigure
    extends AbstractIdeaAction
    implements SpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "misc.xml";
    }


    @Nullable
    private String getDefaultNotNullAnnotation() {
        String result = getIdeaSettings().getNullability().getDefaultNotNullAnnotation();
        return result;
    }

    @Nullable
    private String getDefaultNullableAnnotation() {
        String result = getIdeaSettings().getNullability().getDefaultNullableAnnotation();
        return result;
    }


    @Override
    public boolean isEnabled() {
        return isNotEmpty(getDefaultNotNullAnnotation()) || isNotEmpty(getDefaultNullableAnnotation());
    }

    @Override
    public void execute(XmlProvider xmlProvider) {
        val document = parseJdomDocument(xmlProvider);

        val nullableNotNullManager = ensureJdomElement(document.getRootElement(), "component", singletonMap(
            "name", "NullableNotNullManager"
        ));

        val defaultNotNullAnnotation = getDefaultNotNullAnnotation();
        if (isNotEmpty(defaultNotNullAnnotation)) {
            ensureJdomElement(nullableNotNullManager, "option", singletonMap("name", "myDefaultNotNull"))
                .setAttribute("value", defaultNotNullAnnotation);
        }

        val defaultNullableAnnotation = getDefaultNullableAnnotation();
        if (isNotEmpty(defaultNullableAnnotation)) {
            ensureJdomElement(nullableNotNullManager, "option", singletonMap("name", "myDefaultNullable"))
                .setAttribute("value", defaultNullableAnnotation);
        }

        replaceXmlProviderContentWithJdom(xmlProvider, document);
    }
}
