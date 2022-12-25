package name.remal.gradleplugins.ideasettings;

import static name.remal.gradle_plugins.toolkit.ObjectUtils.doNotInline;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.gradle.api.Project;

@Getter
@Setter
public class IdeaCheckstyleSettings {

    private static final String BUNDLED_CONFIG_FILE_PATH_PREFIX = doNotInline("$;bundled;$:");
    private static final String BUNDLED_SUN_CHECKS_CONFIG_FILE_PATH = BUNDLED_CONFIG_FILE_PATH_PREFIX + "sun";
    private static final String BUNDLED_GOOGLE_CHECKS_CONFIG_FILE_PATH = BUNDLED_CONFIG_FILE_PATH_PREFIX + "google";


    @Nullable
    private String configFilePath;

    /**
     * @param file See {@link Project#file(Object)}.
     */
    public void setConfigFile(@Nullable Object file) {
        this.configFilePath = file != null ? project.file(file).getAbsolutePath() : null;
    }

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


    @Nullable
    private Boolean treatErrorsAsWarnings;


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private final Project project;

    @Inject
    public IdeaCheckstyleSettings(Project project) {
        this.project = project;
    }

}
