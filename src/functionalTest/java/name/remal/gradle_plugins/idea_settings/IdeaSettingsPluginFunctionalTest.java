package name.remal.gradle_plugins.idea_settings;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;

import lombok.RequiredArgsConstructor;
import name.remal.gradle_plugins.toolkit.testkit.functional.GradleProject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class IdeaSettingsPluginFunctionalTest {

    final GradleProject project;

    @BeforeEach
    void beforeEach() throws Throwable {
        project.forBuildFile(build -> {
            build.applyPlugin("name.remal.idea-settings");
        });

        project.withoutConfigurationCache();

        var ideaDir = project.getProjectDir().toPath().resolve(".idea");
        createDirectories(ideaDir);

        var layoutFile = project.getProjectDir().toPath().resolve("layout.json");
        try (var writer = newBufferedWriter(layoutFile, UTF_8)) {
            var layout = new JSONObject();
            layout.put("ideaDirPath", ideaDir.toString());
            layout.write(writer);
        }
    }

    @Test
    void emptyBuildPerformsSuccessfully() {
        project.assertBuildSuccessfully("processIdeaSettings");
    }

    @Test
    void checkstyle() {
        project.forBuildFile(build -> {
            build.applyPlugin("checkstyle");
        });
        project.assertBuildSuccessfully("processIdeaSettings");
    }

}
