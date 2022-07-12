package name.remal.gradleplugins.ideasettings.internal;

import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.ObjectUtils.doNotInline;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@NoArgsConstructor(access = PRIVATE)
abstract class IdeaPlugins {

    public static final String EDITORCONFIG_IDEA_PLUGIN_ID = doNotInline("org.editorconfig.editorconfigjetbrains");

    public static final String IDEA_CHECKSTYLE_IDEA_PLUGIN_ID = doNotInline("CheckStyle-IDEA");

}
