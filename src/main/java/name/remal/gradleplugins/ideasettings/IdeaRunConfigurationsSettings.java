package name.remal.gradleplugins.ideasettings;

import static name.remal.gradle_plugins.toolkit.PropertiesConventionUtils.setPropertyConvention;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;

@Getter
@Setter
public class IdeaRunConfigurationsSettings {

    private final IdeaJavaAppRunConfigurationsSettings javaApplication;

    public void javaApplication(Action<IdeaJavaAppRunConfigurationsSettings> action) {
        action.execute(javaApplication);
    }

    @Getter
    @Setter
    public static class IdeaJavaAppRunConfigurationsSettings {

        private JavaVersion javaVersion = JavaVersion.current();

        private List<String> jvmParameters = new ArrayList<>();

        private Boolean shortenCommandLine;

    }

    @Inject
    public IdeaRunConfigurationsSettings(Project project) {
        this.javaApplication = project.getObjects().newInstance(IdeaJavaAppRunConfigurationsSettings.class);

        setPropertyConvention(
            this.javaApplication,
            "shortenCommandLine",
            it -> it.getJavaVersion().isJava9Compatible()
        );
    }

}
