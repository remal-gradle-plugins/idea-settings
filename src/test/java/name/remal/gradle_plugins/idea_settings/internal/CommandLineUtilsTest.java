package name.remal.gradle_plugins.idea_settings.internal;

import com.google.common.collect.ImmutableList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class CommandLineUtilsTest {

    @Test
    void parseCommandLine_windows() {
        Map<String, List<String>> map = new LinkedHashMap<>();
        map.put("", ImmutableList.of());
        map.put("  ", ImmutableList.of());
        map.put("\"\"", ImmutableList.of(""));
        map.put("\"\"\"\"", ImmutableList.of("\""));
        map.put("\"\"\"\"\"\"", ImmutableList.of("\"\""));
        map.put("CallMeIshmael", ImmutableList.of("CallMeIshmael"));
        map.put("\"Call Me Ishmael\"", ImmutableList.of("Call Me Ishmael"));
        map.put("Cal\"l Me I\"shmael", ImmutableList.of("Call Me Ishmael"));
        map.put("CallMe\\\"Ishmael", ImmutableList.of("CallMe\\Ishmael")); // not ImmutableList.of("CallMe\"Ishmael")
        map.put("\"CallMe\\\"Ishmael\"", ImmutableList.of("CallMe\"Ishmael"));
        map.put("\"Call Me Ishmael\\\\\"", ImmutableList.of("Call Me Ishmael\\"));
        map.put("\"CallMe\\\\\\\"Ishmael\"", ImmutableList.of("CallMe\\\"Ishmael"));
        map.put("a\\\\\\b", ImmutableList.of("a\\\\b")); // not ImmutableList.of("a\\\\\\b")
        map.put("\"a\\\\\\b\"", ImmutableList.of("a\\\\b")); // not ImmutableList.of("a\\\\\\b")
        map.put("\"\\\"Call Me Ishmael\\\"\"", ImmutableList.of("\"Call Me Ishmael\""));
        map.put("\"C:\\TEST A\\\\\"", ImmutableList.of("C:\\TEST A\\"));
        map.put("\"\\\"C:\\TEST A\\\\\\\"\"", ImmutableList.of("\"C:\\TEST A\\\""));
        map.put("\"a b c\"  d  e", ImmutableList.of("a b c", "d", "e"));
        map.put("\"ab\\\"c\"  \"\\\\\"  d", ImmutableList.of("ab\"c", "\\", "d"));
        map.put(
            "a\\\\\\b d\"e f\"g h",
            ImmutableList.of("a\\\\b", "de fg", "h")
        ); // not ImmutableList.of("a\\\\\\b", "de fg", "h")
        map.put("a\\\\\\\"b c d", ImmutableList.of("a\\\\b c d")); // not ImmutableList.of("a\\\"b", "c", "d")
        map.put("a\\\\\\\\\"b c\" d e", ImmutableList.of("a\\\\b c", "d", "e"));
        map.put("\"a b c\"\"", ImmutableList.of("a b c\""));
        map.put("\"\"\"CallMeIshmael\"\"\"  b  c", ImmutableList.of("\"CallMeIshmael\"", "b", "c"));
        map.put("\"\"\"Call Me Ishmael\"\"\"", ImmutableList.of("\"Call Me Ishmael\""));
        map.put("\"\"\"\"Call Me Ishmael\"\" b c", ImmutableList.of("\"Call", "Me", "Ishmael", "b", "c"));
        map.put("'Call Me Ishmael'", ImmutableList.of("'Call", "Me", "Ishmael'"));
        map.put("CallMe\\$Ishmael", ImmutableList.of("CallMe\\$Ishmael"));

        val softly = new SoftAssertions();
        for (val entry : map.entrySet()) {
            val parsedParameters = CommandLineUtils.parseCommandLine(
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
        map.put("", ImmutableList.of());
        map.put("  ", ImmutableList.of());
        map.put("\"\"", ImmutableList.of(""));
        map.put("\"\"\"\"", ImmutableList.of(""));
        map.put("\"\"\"\"\"\"", ImmutableList.of(""));
        map.put("CallMeIshmael", ImmutableList.of("CallMeIshmael"));
        map.put("\"Call Me Ishmael\"", ImmutableList.of("Call Me Ishmael"));
        map.put("Cal\"l Me I\"shmael", ImmutableList.of("Call Me Ishmael"));
        map.put("CallMe\\\"Ishmael", ImmutableList.of("CallMe\\Ishmael"));
        map.put("\"CallMe\\\"Ishmael\"", ImmutableList.of("CallMe\"Ishmael"));
        map.put("\"Call Me Ishmael\\\\\"", ImmutableList.of("Call Me Ishmael\\"));
        map.put("\"CallMe\\\\\\\"Ishmael\"", ImmutableList.of("CallMe\\\"Ishmael"));
        map.put("a\\\\\\b", ImmutableList.of("a\\\\b"));
        map.put("\"a\\\\\\b\"", ImmutableList.of("a\\\\b"));
        map.put("\"\\\"Call Me Ishmael\\\"\"", ImmutableList.of("\"Call Me Ishmael\""));
        map.put("\"C:\\TEST A\\\\\"", ImmutableList.of("C:\\TEST A\\"));
        map.put("\"\\\"C:\\TEST A\\\\\\\"\"", ImmutableList.of("\"C:\\TEST A\\\""));
        map.put("\"a b c\"  d  e", ImmutableList.of("a b c", "d", "e"));
        map.put("\"ab\\\"c\"  \"\\\\\"  d", ImmutableList.of("ab\"c", "\\", "d"));
        map.put("a\\\\\\b d\"e f\"g h", ImmutableList.of("a\\\\b", "de fg", "h"));
        map.put("a\\\\\\\"b c d", ImmutableList.of("a\\\\b c d"));
        map.put("a\\\\\\\\\"b c\" d e", ImmutableList.of("a\\\\b c", "d", "e"));
        map.put("\"a b c\"\"", ImmutableList.of("a b c"));
        map.put("\"\"\"CallMeIshmael\"\"\"  b  c", ImmutableList.of("CallMeIshmael", "b", "c"));
        map.put("\"\"\"Call Me Ishmael\"\"\"", ImmutableList.of("Call Me Ishmael"));
        map.put("\"\"\"\"Call Me Ishmael\"\" b c", ImmutableList.of("Call", "Me", "Ishmael", "b", "c"));
        map.put("'Call Me Ishmael'", ImmutableList.of("Call Me Ishmael"));
        map.put("CallMe\\$Ishmael", ImmutableList.of("CallMe$Ishmael"));

        val softly = new SoftAssertions();
        for (val entry : map.entrySet()) {
            val parsedParameters = CommandLineUtils.parseCommandLine(
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
        map.put(ImmutableList.of(), "");
        map.put(ImmutableList.of(""), "\"\"");
        map.put(ImmutableList.of("", ""), "\"\" \"\"");
        map.put(ImmutableList.of("ab"), "ab");
        map.put(ImmutableList.of("a b"), "\"a b\"");
        map.put(ImmutableList.of("a'b"), "a'b");
        map.put(ImmutableList.of("a\"b"), "\"a\\\"b\"");
        map.put(ImmutableList.of("a\\\"b"), "\"a\\\\\\\"b\"");
        map.put(ImmutableList.of("a$b"), "a$b");

        val softly = new SoftAssertions();
        for (val entry : map.entrySet()) {
            val string = CommandLineUtils.createCommandLine(
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
        map.put(ImmutableList.of(), "");
        map.put(ImmutableList.of(""), "\"\"");
        map.put(ImmutableList.of("", ""), "\"\" \"\"");
        map.put(ImmutableList.of("ab"), "ab");
        map.put(ImmutableList.of("a b"), "\"a b\"");
        map.put(ImmutableList.of("a'b"), "\"a'b\"");
        map.put(ImmutableList.of("a\"b"), "\"a\\\"b\"");
        map.put(ImmutableList.of("a\\\"b"), "\"a\\\\\\\"b\"");
        map.put(ImmutableList.of("a$b"), "\"a\\$b\"");

        val softly = new SoftAssertions();
        for (val entry : map.entrySet()) {
            val string = CommandLineUtils.createCommandLine(
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
