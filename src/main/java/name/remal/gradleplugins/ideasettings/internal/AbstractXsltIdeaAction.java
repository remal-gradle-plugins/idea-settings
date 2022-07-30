package name.remal.gradleplugins.ideasettings.internal;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import java.util.Optional;
import javax.xml.transform.Transformer;
import name.remal.gradleplugins.toolkit.PathUtils;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
abstract class AbstractXsltIdeaAction extends AbstractIdeaAction {

    private static final String IDEA_DIR_PARAM = "idea-dir";

    @OverridingMethodsMustInvokeSuper
    protected void configureTransformer(Transformer transformer) {
        Optional.ofNullable(getIdeaDirOrNull())
            .map(PathUtils::normalizePath)
            .map(Object::toString)
            .ifPresent(it -> transformer.setParameter(IDEA_DIR_PARAM, it));
    }

}
