package name.remal.gradleplugins.ideasettings.internal;

import static com.google.common.primitives.Chars.contains;
import static java.lang.Character.isWhitespace;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.ObjectUtils.isEmpty;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import lombok.NoArgsConstructor;
import lombok.val;
import org.gradle.internal.os.OperatingSystem;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Unmodifiable;

/**
 * See IDEA's implementation of
 * <code><a href="https://github.com/JetBrains/intellij-community/blob/162af589e3c4a9a0423e0bb9b08aef67475cbf61/platform/util/src/com/intellij/util/execution/ParametersListUtil.java">ParametersListUtil</a></code>.
 */
@Internal
@NoArgsConstructor(access = PRIVATE)
abstract class CommandLineUtils {

    private static final OperatingSystem OPERATING_SYSTEM = OperatingSystem.current();

    @VisibleForTesting
    static final char[] WINDOWS_CHARS_TO_ESCAPE = new char[]{};

    @VisibleForTesting
    static final char[] LINUX_CHARS_TO_ESCAPE = new char[]{'$', '`', '\n'};


    @Unmodifiable
    public static List<String> parseCommandLine(@Nullable String string) {
        if (isEmpty(string)) {
            return emptyList();
        }

        final boolean supportSingleQuotes;
        final boolean supportTwoDoubleQuotes;
        final char[] charsToEscape;
        if (OPERATING_SYSTEM.isWindows()) {
            supportSingleQuotes = false;
            supportTwoDoubleQuotes = true;
            charsToEscape = WINDOWS_CHARS_TO_ESCAPE;
        } else {
            supportSingleQuotes = true;
            supportTwoDoubleQuotes = false;
            charsToEscape = LINUX_CHARS_TO_ESCAPE;
        }

        return parseCommandLine(
            string,
            supportSingleQuotes,
            supportTwoDoubleQuotes,
            charsToEscape
        );
    }

    @VisibleForTesting
    @Unmodifiable
    @SuppressWarnings({"java:S3776", "java:S127"})
    static List<String> parseCommandLine(
        String string,
        boolean supportSingleQuotes,
        boolean supportTwoDoubleQuotes,
        char[] charsToEscape
    ) {
        string = string.trim();
        if (string.isEmpty()) {
            return emptyList();
        }

        List<String> result = new ArrayList<>();

        val token = new StringBuilder();
        char quote = 0;

        for (int index = 0; index < string.length(); ++index) {
            val ch = string.charAt(index);

            if (quote == 0 && isWhitespace(ch)) {
                if (token.length() > 0) {
                    result.add(token.toString());
                    token.setLength(0);
                }
                continue;
            }

            if (ch == '\\') {
                if (index == string.length() - 1) {
                    token.append(ch);
                    break;
                }

                val nextCh = string.charAt(index + 1);
                if (nextCh == ch
                    || contains(charsToEscape, nextCh)
                    || (nextCh == quote)
                ) {
                    token.append(nextCh);
                    ++index;
                } else {
                    token.append(ch);
                }
                continue;
            }

            if (supportTwoDoubleQuotes && quote == '"' && ch == '"' && index < string.length() - 1) {
                val nextCh = string.charAt(index + 1);
                if (nextCh == ch) {
                    token.append(nextCh);
                    ++index;
                    continue;
                }
            }

            if (quote == 0) {
                if (ch == '"'
                    || (supportSingleQuotes && ch == '\'')
                ) {
                    quote = ch;
                    continue;
                }
            } else {
                if (ch == quote) {
                    quote = 0;
                    continue;
                }
            }

            token.append(ch);
        }

        result.add(token.toString());

        return unmodifiableList(result);
    }


    public static String createCommandLine(@Nullable Collection<String> parameters) {
        if (isEmpty(parameters)) {
            return "";
        }

        final boolean supportSingleQuotes;
        final char[] charsToEscape;
        if (OPERATING_SYSTEM.isWindows()) {
            supportSingleQuotes = false;
            charsToEscape = WINDOWS_CHARS_TO_ESCAPE;
        } else {
            supportSingleQuotes = true;
            charsToEscape = LINUX_CHARS_TO_ESCAPE;
        }

        return createCommandLine(
            parameters,
            supportSingleQuotes,
            charsToEscape
        );
    }

    @VisibleForTesting
    @SuppressWarnings("java:S3776")
    static String createCommandLine(
        Collection<String> parameters,
        boolean supportSingleQuotes,
        char[] charsToEscape
    ) {
        val result = new StringBuilder();

        for (val parameter : parameters) {
            if (result.length() > 0) {
                result.append(' ');
            }

            if (parameter.isEmpty()) {
                result.append("\"\"");
                continue;
            }

            boolean shouldBeQuoted = false;
            val chars = parameter.toCharArray();
            for (val ch : chars) {
                if (isWhitespace(ch)
                    || ch == '"'
                    || (supportSingleQuotes && ch == '\'')
                    || contains(charsToEscape, ch)
                ) {
                    shouldBeQuoted = true;
                    break;
                }
            }

            if (!shouldBeQuoted) {
                result.append(parameter);
                continue;
            }

            result.append('"');
            for (val ch : chars) {
                if (ch == '"'
                    || ch == '\\'
                    || contains(charsToEscape, ch)
                ) {
                    result.append('\\');
                }
                result.append(ch);
            }
            result.append('"');
        }

        return result.toString();
    }

}
