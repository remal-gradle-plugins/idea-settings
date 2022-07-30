package name.remal.gradleplugins.ideasettings.internal;

import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.xml.XmlProviderUtils.replaceXmlProviderContent;
import static org.jdom2.output.Format.getCompactFormat;

import java.io.StringReader;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.gradle.api.XmlProvider;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.XMLOutputter;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.xml.sax.InputSource;

@Internal
@NoArgsConstructor(access = PRIVATE)
abstract class JdomUtils {

    private static final SAXBuilder JDOM_SAX_BUILDER;

    static {
        val jdomSaxBuilder = new SAXBuilder();
        jdomSaxBuilder.setReuseParser(true);
        jdomSaxBuilder.setXMLReaderFactory(XMLReaders.NONVALIDATING);
        jdomSaxBuilder.setEntityResolver((publicId, systemId) -> new InputSource(new StringReader("")));
        JDOM_SAX_BUILDER = jdomSaxBuilder;
    }

    @SneakyThrows
    public static Document parseJdomDocument(String content) {
        return JDOM_SAX_BUILDER.build(new StringReader(content));
    }

    public static Document parseJdomDocument(XmlProvider xmlProvider) {
        return parseJdomDocument(xmlProvider.asString().toString());
    }


    private static final XMLOutputter COMPACT_XML_OUTPUTTER = new XMLOutputter(getCompactFormat());

    public static String compactJdomString(Document document) {
        return COMPACT_XML_OUTPUTTER.outputString(document);
    }

    public static void replaceXmlProviderContentWithJdom(XmlProvider xmlProvider, Document document) {
        val content = compactJdomString(document);
        replaceXmlProviderContent(xmlProvider, content);
    }

}
