package name.remal.gradleplugins.ideasettings.internal;

import static com.google.common.reflect.Reflection.getPackageName;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.METHOD;
import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.ResourceUtils.readResource;
import static name.remal.gradleplugins.toolkit.xml.XmlProviderUtils.replaceXmlProviderContent;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.CustomLog;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import name.remal.gradle_plugins.api.RelocatePackages;
import net.sf.saxon.jaxp.SaxonTransformerFactory;
import net.sf.saxon.lib.Logger;
import org.gradle.api.XmlProvider;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@NoArgsConstructor(access = PRIVATE)
@RelocatePackages("net.sf.saxon")
@CustomLog
abstract class XslUtils {

    private static final String CLASSPATH_PROTOCOL = "classpath";

    @SneakyThrows
    public static void transformXmlProvider(
        XmlProvider xmlProvider,
        @Language("file-reference") String resourceName,
        Consumer<Transformer> transformerConfigurer
    ) {
        val transformer = compileTransformer(resourceName);
        transformer.setOutputProperty(METHOD, "xml");
        transformer.setOutputProperty(INDENT, "true");
        transformerConfigurer.accept(transformer);

        val sourceXmlString = xmlProvider.asString().toString();
        val source = new StreamSource(new StringReader(sourceXmlString));

        val stringWriter = new StringWriter();
        val result = new StreamResult(stringWriter);

        transformer.transform(source, result);

        val resultXmlString = stringWriter.toString();
        replaceXmlProviderContent(xmlProvider, resultXmlString);
    }

    public static void transformXmlProvider(
        XmlProvider xmlProvider,
        @Language("file-reference") String resourceName
    ) {
        transformXmlProvider(
            xmlProvider,
            resourceName,
            __ -> { }
        );
    }


    @SneakyThrows
    private static Transformer compileTransformer(@Language("file-reference") String resourceName) {
        if (!resourceName.startsWith("/")) {
            //noinspection InjectedReferences
            resourceName = '/' + getPackageName(XslUtils.class).replace('.', '/') + '/' + resourceName;
        }

        val transformerSource = resolveUri(CLASSPATH_PROTOCOL + ':' + resourceName);
        return TRANSFORMER_FACTORY.newTransformer(transformerSource);
    }

    private static final TransformerFactory TRANSFORMER_FACTORY;

    static {
        val transformerFactory = new SaxonTransformerFactory();

        transformerFactory.setErrorListener(new ErrorListener() {
            @Override
            public void fatalError(TransformerException exception) throws TransformerException {
                throw exception;
            }

            @Override
            public void error(TransformerException exception) throws TransformerException {
                fatalError(exception);
            }

            @Override
            public void warning(TransformerException exception) throws TransformerException {
                error(exception);
            }
        });

        transformerFactory.setURIResolver(XslUtils::resolveUri);

        transformerFactory.getConfiguration().setLogger(new Logger() {
            @Override
            @SneakyThrows
            public void println(String message, int severity) {
                if (severity <= Logger.INFO) {
                    logger.lifecycle(message);
                } else {
                    throw new TransformerException(message);
                }
            }
        });

        TRANSFORMER_FACTORY = transformerFactory;
    }


    @SneakyThrows
    private static Source resolveUri(String href, @Nullable String base) throws TransformerException {
        try {
            val hrefUri = new URI(href);

            final URI resultUri;
            if (base == null) {
                if (!CLASSPATH_PROTOCOL.equals(hrefUri.getScheme())) {
                    throw new TransformerException("Not a classpath href: " + href);
                }
                resultUri = hrefUri;

            } else {
                val baseUri = new URI(base);
                if (hrefUri.isAbsolute()) {
                    throw new TransformerException("Not a relative href: " + href);
                } else if (!CLASSPATH_PROTOCOL.equals(baseUri.getScheme())) {
                    throw new TransformerException("Not a classpath base: " + baseUri);
                }
                resultUri = baseUri.resolve(hrefUri);
            }

            val resourceName = resultUri.getPath();
            val resourceBytes = readResource(resourceName, XslUtils.class);
            return new StreamSource(new ByteArrayInputStream(resourceBytes), resultUri.toString());

        } catch (Throwable e) {
            throw e instanceof TransformerException
                ? (TransformerException) e
                : new TransformerException(e);
        }
    }

    private static Source resolveUri(String href) throws TransformerException {
        return resolveUri(href, null);
    }

}
