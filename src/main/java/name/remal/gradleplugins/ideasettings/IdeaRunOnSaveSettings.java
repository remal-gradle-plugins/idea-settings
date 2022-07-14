package name.remal.gradleplugins.ideasettings;

import static name.remal.gradleplugins.ideasettings.IdeaSettings.setStringsCollectionFromIterable;

import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nullable;
import javax.inject.Inject;
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

    private final Set<String> enabledCustomPluginActions = new TreeSet<>();

    public void setEnabledCustomPluginActions(Iterable<? extends CharSequence> enabledCustomPluginActions) {
        setStringsCollectionFromIterable(this.enabledCustomPluginActions, enabledCustomPluginActions);
    }

    private final Set<String> disabledCustomPluginActions = new TreeSet<>();

    public void setDisabledCustomPluginActions(Iterable<? extends CharSequence> disabledCustomPluginActions) {
        setStringsCollectionFromIterable(this.disabledCustomPluginActions, disabledCustomPluginActions);
    }


    @Inject
    public IdeaRunOnSaveSettings() {
        // do nothing
    }

}
