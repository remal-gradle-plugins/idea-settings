package name.remal.gradle_plugins.idea_settings;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdeaDatabaseSettings {

    @Nullable
    private String defaultDialect;


    @Inject
    public IdeaDatabaseSettings() {
        // do nothing
    }

}
