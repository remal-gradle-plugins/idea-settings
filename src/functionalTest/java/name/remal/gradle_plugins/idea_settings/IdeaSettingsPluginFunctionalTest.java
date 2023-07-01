package name.remal.gradle_plugins.idea_settings;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;

import lombok.RequiredArgsConstructor;
import lombok.val;
import name.remal.gradle_plugins.toolkit.testkit.functional.GradleProject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class IdeaSettingsPluginFunctionalTest {

    private final GradleProject project;

    @BeforeEach
    void beforeEach() throws Throwable {
        project.forBuildFile(build -> {
            build.applyPlugin("name.remal.idea-settings");
            build.registerDefaultTask("processIdeaSettings");
        });

        project.withoutConfigurationCache();

        val ideaDir = project.getProjectDir().toPath().resolve(".idea");
        createDirectories(ideaDir);

        val layoutFile = project.getProjectDir().toPath().resolve("layout.json");
        try (val writer = newBufferedWriter(layoutFile, UTF_8)) {
            val layout = new JSONObject();
            layout.put("ideaDirPath", ideaDir.toString());
            layout.write(writer);
        }
    }

    @Test
    void emptyBuildPerformsSuccessfully() {
        project.assertBuildSuccessfully();
    }

    @Test
    void checkstyle() {
        project.forBuildFile(build -> {
            build.applyPlugin("checkstyle");
        });
        project.assertBuildSuccessfully();
    }

}
