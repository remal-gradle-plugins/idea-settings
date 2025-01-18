package name.remal.gradle_plugins.idea_settings.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class CommandLineUtilsTest {

    @Test
    void parseCommandLine_windows() {
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put("", List.of());
        map.put("  ", List.of());
        map.put("\"\"", List.of(""));
        map.put("\"\"\"\"", List.of("\""));
        map.put("\"\"\"\"\"\"", List.of("\"\""));
        map.put("CallMeIshmael", List.of("CallMeIshmael"));
        map.put("\"Call Me Ishmael\"", List.of("Call Me Ishmael"));
        map.put("Cal\"l Me I\"shmael", List.of("Call Me Ishmael"));
        map.put("CallMe\\\"Ishmael", List.of("CallMe\\Ishmael")); // not List.of("CallMe\"Ishmael")
        map.put("\"CallMe\\\"Ishmael\"", List.of("CallMe\"Ishmael"));
        map.put("\"Call Me Ishmael\\\\\"", List.of("Call Me Ishmael\\"));
        map.put("\"CallMe\\\\\\\"Ishmael\"", List.of("CallMe\\\"Ishmael"));
        map.put("a\\\\\\b", List.of("a\\\\b")); // not List.of("a\\\\\\b")
        map.put("\"a\\\\\\b\"", List.of("a\\\\b")); // not List.of("a\\\\\\b")
        map.put("\"\\\"Call Me Ishmael\\\"\"", List.of("\"Call Me Ishmael\""));
        map.put("\"C:\\TEST A\\\\\"", List.of("C:\\TEST A\\"));
        map.put("\"\\\"C:\\TEST A\\\\\\\"\"", List.of("\"C:\\TEST A\\\""));
        map.put("\"a b c\"  d  e", List.of("a b c", "d", "e"));
        map.put("\"ab\\\"c\"  \"\\\\\"  d", List.of("ab\"c", "\\", "d"));
        map.put(
            "a\\\\\\b d\"e f\"g h",
            List.of("a\\\\b", "de fg", "h")
        ); // not List.of("a\\\\\\b", "de fg", "h")
        map.put("a\\\\\\\"b c d", List.of("a\\\\b c d")); // not List.of("a\\\"b", "c", "d")
        map.put("a\\\\\\\\\"b c\" d e", List.of("a\\\\b c", "d", "e"));
        map.put("\"a b c\"\"", List.of("a b c\""));
        map.put("\"\"\"CallMeIshmael\"\"\"  b  c", List.of("\"CallMeIshmael\"", "b", "c"));
        map.put("\"\"\"Call Me Ishmael\"\"\"", List.of("\"Call Me Ishmael\""));
        map.put("\"\"\"\"Call Me Ishmael\"\" b c", List.of("\"Call", "Me", "Ishmael", "b", "c"));
        map.put("'Call Me Ishmael'", List.of("'Call", "Me", "Ishmael'"));
        map.put("CallMe\\$Ishmael", List.of("CallMe\\$Ishmael"));

        var softly = new SoftAssertions();
        for (var entry : map.entrySet()) {
            var parsedParameters = CommandLineUtils.parseCommandLine(
                entry.getKey(),
                false,
                true,
                CommandLineUtils.WINDOWS_CHARS_TO_ESCAPE
            );
            softly.assertThat(parsedParameters).as(entry.getKey())
                .isEqualTo(entry.getValue());
        }
        softly.assertAll();
    }

    @Test
    void parseCommandLine_linux() {
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put("", List.of());
        map.put("  ", List.of());
        map.put("\"\"", List.of(""));
        map.put("\"\"\"\"", List.of(""));
        map.put("\"\"\"\"\"\"", List.of(""));
        map.put("CallMeIshmael", List.of("CallMeIshmael"));
        map.put("\"Call Me Ishmael\"", List.of("Call Me Ishmael"));
        map.put("Cal\"l Me I\"shmael", List.of("Call Me Ishmael"));
        map.put("CallMe\\\"Ishmael", List.of("CallMe\\Ishmael"));
        map.put("\"CallMe\\\"Ishmael\"", List.of("CallMe\"Ishmael"));
        map.put("\"Call Me Ishmael\\\\\"", List.of("Call Me Ishmael\\"));
        map.put("\"CallMe\\\\\\\"Ishmael\"", List.of("CallMe\\\"Ishmael"));
        map.put("a\\\\\\b", List.of("a\\\\b"));
        map.put("\"a\\\\\\b\"", List.of("a\\\\b"));
        map.put("\"\\\"Call Me Ishmael\\\"\"", List.of("\"Call Me Ishmael\""));
        map.put("\"C:\\TEST A\\\\\"", List.of("C:\\TEST A\\"));
        map.put("\"\\\"C:\\TEST A\\\\\\\"\"", List.of("\"C:\\TEST A\\\""));
        map.put("\"a b c\"  d  e", List.of("a b c", "d", "e"));
        map.put("\"ab\\\"c\"  \"\\\\\"  d", List.of("ab\"c", "\\", "d"));
        map.put("a\\\\\\b d\"e f\"g h", List.of("a\\\\b", "de fg", "h"));
        map.put("a\\\\\\\"b c d", List.of("a\\\\b c d"));
        map.put("a\\\\\\\\\"b c\" d e", List.of("a\\\\b c", "d", "e"));
        map.put("\"a b c\"\"", List.of("a b c"));
        map.put("\"\"\"CallMeIshmael\"\"\"  b  c", List.of("CallMeIshmael", "b", "c"));
        map.put("\"\"\"Call Me Ishmael\"\"\"", List.of("Call Me Ishmael"));
        map.put("\"\"\"\"Call Me Ishmael\"\" b c", List.of("Call", "Me", "Ishmael", "b", "c"));
        map.put("'Call Me Ishmael'", List.of("Call Me Ishmael"));
        map.put("CallMe\\$Ishmael", List.of("CallMe$Ishmael"));

        var softly = new SoftAssertions();
        for (var entry : map.entrySet()) {
            var parsedParameters = CommandLineUtils.parseCommandLine(
                entry.getKey(),
                true,
                false,
                CommandLineUtils.LINUX_CHARS_TO_ESCAPE
            );
            softly.assertThat(parsedParameters).as(entry.getKey())
                .isEqualTo(entry.getValue());
        }
        softly.assertAll();
    }


    @Test
    void createCommandLine_windows() {
        Map<List<String>, String> map = new LinkedHashMap<>();
        map.put(List.of(), "");
        map.put(List.of(""), "\"\"");
        map.put(List.of("", ""), "\"\" \"\"");
        map.put(List.of("ab"), "ab");
        map.put(List.of("a b"), "\"a b\"");
        map.put(List.of("a'b"), "a'b");
        map.put(List.of("a\"b"), "\"a\\\"b\"");
        map.put(List.of("a\\\"b"), "\"a\\\\\\\"b\"");
        map.put(List.of("a$b"), "a$b");

        var softly = new SoftAssertions();
        for (var entry : map.entrySet()) {
            var string = CommandLineUtils.createCommandLine(
                entry.getKey(),
                false,
                CommandLineUtils.WINDOWS_CHARS_TO_ESCAPE
            );
            softly.assertThat(string).as(entry.getKey().toString())
                .isEqualTo(entry.getValue());
        }
        softly.assertAll();
    }

    @Test
    void createCommandLine_linux() {
        Map<List<String>, String> map = new LinkedHashMap<>();
        map.put(List.of(), "");
        map.put(List.of(""), "\"\"");
        map.put(List.of("", ""), "\"\" \"\"");
        map.put(List.of("ab"), "ab");
        map.put(List.of("a b"), "\"a b\"");
        map.put(List.of("a'b"), "\"a'b\"");
        map.put(List.of("a\"b"), "\"a\\\"b\"");
        map.put(List.of("a\\\"b"), "\"a\\\\\\\"b\"");
        map.put(List.of("a$b"), "\"a\\$b\"");

        var softly = new SoftAssertions();
        for (var entry : map.entrySet()) {
            var string = CommandLineUtils.createCommandLine(
                entry.getKey(),
                true,
                CommandLineUtils.LINUX_CHARS_TO_ESCAPE
            );
            softly.assertThat(string).as(entry.getKey().toString())
                .isEqualTo(entry.getValue());
        }
        softly.assertAll();
    }

}
