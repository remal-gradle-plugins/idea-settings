package name.remal.gradleplugins.ideasettings;

import java.util.Collection;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Data;
import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;

@Data
public class IdeaRunConfigurationsSettings {

    private final IdeaJavaApplicationRunConfigurationsSettings javaApplication;

    public void java(Action<IdeaJavaApplicationRunConfigurationsSettings> action) {
        action.execute(javaApplication);
    }

    @Data
    public static class IdeaJavaApplicationRunConfigurationsSettings {

        private Boolean shortenCommandLine = JavaVersion.VERSION_1_9.isJava9Compatible();

    }


    private final IdeaSpringBootApplicationRunConfigurationsSettings springBootApplication;

    public void springBoot(Action<IdeaSpringBootApplicationRunConfigurationsSettings> action) {
        action.execute(springBootApplication);
    }

    @Data
    public static class IdeaSpringBootApplicationRunConfigurationsSettings {

        @Nullable
        private Collection<String> activeProfiles;

    }


    @Inject
    public IdeaRunConfigurationsSettings(Project project) {
        this.javaApplication = project.getObjects().newInstance(IdeaJavaApplicationRunConfigurationsSettings.class);
        this.springBootApplication = project.getObjects()
            .newInstance(IdeaSpringBootApplicationRunConfigurationsSettings.class);
    }

}
