package name.remal.gradle_plugins.idea_settings.internal;

import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradle_plugins.toolkit.ObjectUtils.doNotInline;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@NoArgsConstructor(access = PRIVATE)
abstract class AllIdeaPlugins {

    public static final String EDITORCONFIG_IDEA_PLUGIN_ID = doNotInline("org.editorconfig.editorconfigjetbrains");

    public static final String CHECKSTYLE_IDEA_PLUGIN_ID = doNotInline("CheckStyle-IDEA");

    public static final String SAVE_ACTIONS_IDEA_PLUGIN_ID = doNotInline("com.dubreuia");

}
