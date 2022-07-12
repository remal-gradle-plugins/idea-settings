package name.remal.gradleplugins.ideasettings;

import static name.remal.gradleplugins.toolkit.ObjectUtils.doNotInline;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Data;
import org.gradle.api.model.ObjectFactory;

@Data
public class IdeaCheckstyleSettings {

    private static final String BUNDLED_CONFIG_FILE_PATH_PREFIX = doNotInline("$;bundled;$:");
    private static final String BUNDLED_SUN_CHECKS_CONFIG_FILE_PATH = BUNDLED_CONFIG_FILE_PATH_PREFIX + "sun";
    private static final String BUNDLED_GOOGLE_CHECKS_CONFIG_FILE_PATH = BUNDLED_CONFIG_FILE_PATH_PREFIX + "google";


    @Nullable
    private String configFilePath;

    public void useBundledSunChecks() {
        setConfigFilePath(BUNDLED_SUN_CHECKS_CONFIG_FILE_PATH);
    }

    public boolean isBundledSunChecksEnabled() {
        return BUNDLED_SUN_CHECKS_CONFIG_FILE_PATH.equals(getConfigFilePath());
    }

    public void useBundledGoogleChecks() {
        setConfigFilePath(BUNDLED_GOOGLE_CHECKS_CONFIG_FILE_PATH);
    }

    public boolean isBundledGoogleChecksEnabled() {
        return BUNDLED_GOOGLE_CHECKS_CONFIG_FILE_PATH.equals(getConfigFilePath());
    }


    @Inject
    public IdeaCheckstyleSettings(ObjectFactory objectFactory) {
    }

}
