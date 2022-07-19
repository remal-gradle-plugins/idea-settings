package name.remal.gradleplugins.ideasettings;

import javax.annotation.Nullable;
import lombok.Data;

@Data
public class IdeaRunOnSaveSettings {

    public enum ReformatMode {
        DISABLED, WHOLE_FILE, CHANGED_LINES
    }

    @Nullable
    private ReformatMode reformatMode;

    @Nullable
    private Boolean optimizeImports;


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
