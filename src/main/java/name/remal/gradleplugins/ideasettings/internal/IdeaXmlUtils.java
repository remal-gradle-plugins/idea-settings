package name.remal.gradleplugins.ideasettings.internal;

import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.xml.DomUtils.getNodeChildren;
import static name.remal.gradleplugins.toolkit.xml.DomUtils.getNodeOwnerDocument;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@Internal
@NoArgsConstructor(access = PRIVATE)
abstract class IdeaXmlUtils {

    private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

    @SneakyThrows
    public static Document newDocument() {
        return DOCUMENT_BUILDER_FACTORY.newDocumentBuilder().newDocument();
    }

    public static Element getOrCreateElement(Node parentNode, String componentName) {
        if (parentNode instanceof Document) {
            parentNode = ((Document) parentNode).getDocumentElement();
        }

        Element componentElement = findComponentElement(parentNode, componentName);
        if (componentElement != null) {
            return componentElement;
        }

        componentElement = getNodeOwnerDocument(parentNode).createElement("component");
        componentElement.setAttribute("name", componentName);
        parentNode.appendChild(componentElement);
        return componentElement;
    }

    public static void forComponentElement(Node node, String componentName, Consumer<Element> action) {
        val componentElement = findComponentElement(node, componentName);
        if (componentElement != null) {
            action.accept(componentElement);
        }
    }

    @Nullable
    public static Element findComponentElement(Node node, String componentName) {
        if (node instanceof Document) {
            node = ((Document) node).getDocumentElement();
        }

        return Stream.concat(
                Stream.of(node),
                getNodeChildren(node).stream()
            )
            .filter(Element.class::isInstance)
            .map(Element.class::cast)
            .filter(localNameIs("component"))
            .filter(attributeValueIs("name", componentName))
            .findFirst()
            .orElse(null);
    }

    public static Predicate<Node> localNameIs(String value) {
        return node -> {
            final String name;
            if (node instanceof Element) {
                name = ((Element) node).getTagName();
            } else {
                name = node.getLocalName();
            }
            return Objects.equals(name, value);
        };
    }

    public static Predicate<Node> attributeValueIs(String attrName, String value) {
        return node -> {
            val attrValue = getAttributeValue(node, attrName);
            return Objects.equals(attrValue, value);
        };
    }

    public static Predicate<Node> attributeValueIsNot(String attrName, String value) {
        return attributeValueIs(attrName, value).negate();
    }

    @Nullable
    public static String getAttributeValue(Node node, String attrName) {
        if (node instanceof Element) {
            val element = (Element) node;
            val attr = element.getAttributeNode(attrName);
            if (attr != null) {
                return attr.getValue();
            }
        }
        return null;
    }

}
