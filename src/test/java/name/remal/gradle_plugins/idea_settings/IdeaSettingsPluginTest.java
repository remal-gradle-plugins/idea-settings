package name.remal.gradle_plugins.idea_settings;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newBufferedWriter;
import static name.remal.gradle_plugins.toolkit.reflection.ReflectionUtils.packageNameOf;
import static name.remal.gradle_plugins.toolkit.reflection.ReflectionUtils.unwrapGeneratedSubclass;
import static name.remal.gradle_plugins.toolkit.testkit.ProjectValidations.executeAfterEvaluateActions;
import static name.remal.gradle_plugins.toolkit.testkit.TaskValidations.executeActions;
import static name.remal.gradle_plugins.toolkit.testkit.TaskValidations.executeOnlyIfSpecs;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.RequiredArgsConstructor;
import lombok.val;
import name.remal.gradle_plugins.toolkit.testkit.TaskValidations;
import org.gradle.api.Project;
import org.gradle.api.plugins.quality.CheckstylePlugin;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class IdeaSettingsPluginTest {

    final Project project;

    @BeforeEach
    void beforeEach() throws Throwable {
        val ideaDir = project.getProjectDir().toPath().resolve(".idea");
        createDirectories(ideaDir);

        val layoutFile = project.getProjectDir().toPath().resolve("layout.json");
        try (val writer = newBufferedWriter(layoutFile, UTF_8)) {
            val layout = new JSONObject();
            layout.put("ideaDirPath", ideaDir.toString());
            layout.write(writer);
        }


        project.getPluginManager().apply(IdeaSettingsPlugin.class);
    }

    @Test
    void pluginTasksDoNotHavePropertyProblems() {
        executeAfterEvaluateActions(project);

        val taskClassNamePrefix = packageNameOf(IdeaSettingsPluginTest.class) + '.';
        project.getTasks().stream()
            .filter(task -> {
                val taskClass = unwrapGeneratedSubclass(task.getClass());
                return taskClass.getName().startsWith(taskClassNamePrefix);
            })
            .forEach(TaskValidations::assertNoTaskPropertiesProblems);
    }

    @Test
    void simple() {
        assertDoesNotThrow(() -> executeAfterEvaluateActions(project));
        assertDoesNotThrow(this::executeProcessIdeaSettingsTask);
    }

    @Test
    void checkstyle() {
        project.getPluginManager().apply(CheckstylePlugin.class);
        assertDoesNotThrow(() -> executeAfterEvaluateActions(project));
        assertDoesNotThrow(this::executeProcessIdeaSettingsTask);
    }


    private void executeProcessIdeaSettingsTask() {
        val processIdeaSettings = project.getTasks().getByName("processIdeaSettings");
        assertTrue(executeOnlyIfSpecs(processIdeaSettings));
        executeActions(processIdeaSettings);
    }

}
