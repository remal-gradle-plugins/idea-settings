package name.remal.gradleplugins.ideasettings.internal;

import java.net.URI;
import java.util.Map;
import javax.xml.transform.Transformer;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@RequiredArgsConstructor
public class XsltFileIdeaXmlFileInitializer extends AbstractXsltIdeaXmlFileInitializer {

    private final URI templateUri;
    private final Map<String, Object> templateParams;

    @Override
    protected URI getTemplateUri() {
        return templateUri;
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        templateParams.forEach(transformer::setParameter);
    }

}
