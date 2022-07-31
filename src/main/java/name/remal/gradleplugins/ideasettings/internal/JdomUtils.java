package name.remal.gradleplugins.ideasettings.internal;

import static java.util.Collections.emptyMap;
import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.xml.XmlProviderUtils.replaceXmlProviderContent;
import static org.jdom2.output.Format.getCompactFormat;

import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.gradle.api.XmlProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.ElementFilter;
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


    public static Element ensureJdomElement(Element parentNode, String elementName) {
        return ensureJdomElement(parentNode, elementName, emptyMap());
    }

    public static Element ensureJdomElement(Element parentNode, String elementName, Map<String, Object> attrs) {
        val element = findJdomElement(parentNode, elementName, attrs);
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


    public static void detachJdomElement(Element parentNode, String elementName) {
        detachJdomElement(parentNode, elementName, emptyMap());
    }

    public static void detachJdomElement(Element parentNode, String elementName, Map<String, Object> attrs) {
        Optional.ofNullable(findJdomElement(parentNode, elementName, attrs))
            .ifPresent(Element::detach);
    }


    @Nullable
    public static Element findJdomElement(Element parentNode, String elementName) {
        return findJdomElement(parentNode, elementName, emptyMap());
    }

    @Nullable
    public static Element findJdomElement(Element parentNode, String elementName, Map<String, Object> attrs) {
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
