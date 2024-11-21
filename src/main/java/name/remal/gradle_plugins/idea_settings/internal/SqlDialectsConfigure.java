package name.remal.gradle_plugins.idea_settings.internal;

import static name.remal.gradle_plugins.toolkit.ResourceUtils.getResourceUrl;

import com.google.auto.service.AutoService;
import java.net.URI;
import javax.xml.transform.Transformer;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@AutoService(SpecificIdeaXmlFileProcessor.class)
public class SqlDialectsConfigure extends AbstractXsltSpecificIdeaXmlFileProcessor {

    @Override
    public String getRelativeFilePath() {
        return "sqldialects.xml";
    }

    @Override
    @SneakyThrows
    protected URI getTemplateUri() {
        return getResourceUrl("configure-sql-dialects.xsl").toURI();
    }

    @Override
    protected void configureTransformer(Transformer transformer) {
        super.configureTransformer(transformer);

        val databaseSettings = getIdeaSettings().getDatabase();
        transformer.setParameter("default-dialect", String.valueOf(databaseSettings.getDefaultDialect()));
    }

}
