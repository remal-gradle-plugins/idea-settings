package name.remal.gradleplugins.ideasettings.internal;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.METHOD;
import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.ResourceUtils.readResource;
import static name.remal.gradleplugins.toolkit.xml.DomUtils.getNodeOwnerDocument;
import static name.remal.gradleplugins.toolkit.xml.XmlProviderUtils.replaceXmlProviderContent;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
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
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Document;

@Internal
@NoArgsConstructor(access = PRIVATE)
@RelocatePackages("net.sf.saxon")
@CustomLog
abstract class XsltUtils {

    private static final String CLASSPATH_PROTOCOL = "classpath";
    private static final String FILE_PROTOCOL = "file";
    private static final String JAR_PROTOCOL = "jar";

    @SneakyThrows
    public static void transformXmlProvider(
        XmlProvider xmlProvider,
        URI templateUri,
        Consumer<Transformer> transformerConfigurer
    ) {
        val transformer = compileTransformer(templateUri);
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
        URI templateUri
    ) {
        transformXmlProvider(
            xmlProvider,
            templateUri,
            __ -> { }
        );
    }

    @SneakyThrows
    public static void transformXmlProvider(
        XmlProvider xmlProvider,
        URL templateUrl,
        Consumer<Transformer> transformerConfigurer
    ) {
        transformXmlProvider(
            xmlProvider,
            templateUrl.toURI(),
            transformerConfigurer
        );
    }

    @SneakyThrows
    public static void transformXmlProvider(
        XmlProvider xmlProvider,
        URL templateUrl
    ) {
        transformXmlProvider(
            xmlProvider,
            templateUrl,
            __ -> { }
        );
    }


    @SneakyThrows
    public static Document generateDocumentWithXslt(URI templateUri, Consumer<Transformer> transformerConfigurer) {
        val transformer = compileTransformer(templateUri);
        transformerConfigurer.accept(transformer);

        val result = new DOMResult();
        transformer.transform(new DOMSource(), result);
        return getNodeOwnerDocument(result.getNode());
    }

    public static Document generateDocumentWithXslt(URI templateUri) {
        return generateDocumentWithXslt(templateUri, __ -> { });
    }

    @SneakyThrows
    public static Document generateDocumentWithXslt(URL templateUrl, Consumer<Transformer> transformerConfigurer) {
        return generateDocumentWithXslt(templateUrl.toURI(), transformerConfigurer);
    }

    public static Document generateDocumentWithXslt(URL templateUrl) {
        return generateDocumentWithXslt(templateUrl, __ -> { });
    }


    @SneakyThrows
    private static Transformer compileTransformer(URI templateUri) {
        val transformerSource = resolveUri(templateUri.toString());
        val transformer = TRANSFORMER_FACTORY.newTransformer(transformerSource);
        transformer.setOutputProperty(METHOD, "xml");
        transformer.setOutputProperty(INDENT, "true");
        transformer.setParameter("line-separator", lineSeparator());
        return transformer;
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

        transformerFactory.setURIResolver(XsltUtils::resolveUri);

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


    private static Source resolveUri(String href) throws TransformerException {
        return resolveUri(href, null);
    }

    @SneakyThrows
    private static Source resolveUri(String href, @Nullable String base) throws TransformerException {
        try {
            val hrefUri = new URI(href);

            final URI resultUri;
            if (base == null) {
                checkProtocol("href", hrefUri);
                resultUri = hrefUri;

            } else {
                val baseUri = new URI(base);
                if (hrefUri.isAbsolute()) {
                    throw new TransformerException("Not a relative href: " + href);
                } else {
                    checkProtocol("base", baseUri);
                }

                if (JAR_PROTOCOL.equals(baseUri.getScheme())) {
                    val baseSubUri = new URI(baseUri.getSchemeSpecificPart());
                    String baseFilePath = baseSubUri.getPath();
                    String baseEntryName = "";
                    int delimPos = baseFilePath.indexOf('!');
                    if (delimPos < 0) {
                        baseEntryName = "/";
                    } else {
                        baseEntryName = baseFilePath.substring(delimPos + 1);
                        baseFilePath = baseFilePath.substring(0, delimPos);
                    }

                    val resultPath = new URI(baseEntryName).resolve(hrefUri.getPath()).toString();
                    resultUri = new URI(format(
                        "%s:%s:%s!%s",
                        JAR_PROTOCOL,
                        baseSubUri.getScheme(),
                        baseFilePath,
                        resultPath
                    ));

                } else {
                    resultUri = baseUri.resolve(hrefUri);
                }
            }

            checkProtocol("result", resultUri);

            final byte[] resourceBytes;
            if (CLASSPATH_PROTOCOL.equals(resultUri.getScheme())) {
                val resourceName = resultUri.getPath();
                resourceBytes = readResource(resourceName, XsltUtils.class);
            } else {
                val url = resultUri.toURL();
                val connection = url.openConnection();
                connection.setUseCaches(false);
                try (val inputStream = connection.getInputStream()) {
                    resourceBytes = toByteArray(inputStream);
                }
            }

            return new StreamSource(new ByteArrayInputStream(resourceBytes), resultUri.toString());

        } catch (Throwable e) {
            throw e instanceof TransformerException
                ? (TransformerException) e
                : new TransformerException(e);
        }
    }

    @SneakyThrows
    private static void checkProtocol(String context, URI uri) {
        if (!uri.isAbsolute()) {
            throw new TransformerException(format(
                "Not a relative %s URI: %s",
                context,
                uri
            ));
        }

        val scheme = uri.getScheme();
        if (JAR_PROTOCOL.equals(scheme)) {
            val subUri = new URI(uri.getSchemeSpecificPart());
            checkProtocol(context, subUri);
            return;
        }

        if (!CLASSPATH_PROTOCOL.equals(scheme)
            && !FILE_PROTOCOL.equals(scheme)
        ) {
            throw new TransformerException(format(
                "Not supported protocol for %s URI: %s",
                context,
                uri
            ));
        }
    }

}
