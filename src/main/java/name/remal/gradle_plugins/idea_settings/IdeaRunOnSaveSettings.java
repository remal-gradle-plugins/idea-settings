package name.remal.gradle_plugins.idea_settings;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdeaRunOnSaveSettings {

    public enum ReformatMode {
        DISABLED, WHOLE_FILE, CHANGED_LINES
    }

    @Nullable
    private ReformatMode reformatMode;

    @Nullable
    private Boolean optimizeImports;


    @Inject
    public IdeaRunOnSaveSettings() {
        // do nothing
    }


    /*
    private final Set<String> enabledCustomPluginActions = new TreeSet<>();

    public void setEnabledCustomPluginActions(Iterable<? extends CharSequence> enabledCustomPluginActions) {
        setStringsCollectionFromIterable(this.enabledCustomPluginActions, enabledCustomPluginActions);
    }

    private final Set<String> disabledCustomPluginActions = new TreeSet<>();

    public void setDisabledCustomPluginActions(Iterable<? extends CharSequence> disabledCustomPluginActions) {
        setStringsCollectionFromIterable(this.disabledCustomPluginActions, disabledCustomPluginActions);
    }
    */

}
