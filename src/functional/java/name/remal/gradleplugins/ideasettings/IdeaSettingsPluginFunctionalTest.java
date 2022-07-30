package name.remal.gradleplugins.ideasettings;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;

import lombok.RequiredArgsConstructor;
import lombok.val;
import name.remal.gradleplugins.toolkit.testkit.functional.GradleProject;
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
