package name.remal.gradleplugins.ideasettings;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.RequiredArgsConstructor;
import org.gradle.api.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@RequiredArgsConstructor
class IdeaSettingsPluginTest {

    final Project project;

    @BeforeEach
    void beforeEach() {
        project.getPluginManager().apply(IdeaSettingsPlugin.class);
    }

    @Test
    void test() {
        assertTrue(project.getPlugins().hasPlugin(IdeaSettingsPlugin.class));
    }

}
