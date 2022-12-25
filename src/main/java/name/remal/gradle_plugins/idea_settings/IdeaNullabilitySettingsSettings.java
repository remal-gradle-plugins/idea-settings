package name.remal.gradle_plugins.idea_settings;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IdeaNullabilitySettingsSettings {

    @Nullable
    private String defaultNotNullAnnotation;

    @Nullable
    private String defaultNullableAnnotation;


    @Inject
    public IdeaNullabilitySettingsSettings() {
        // do nothing
    }

}
