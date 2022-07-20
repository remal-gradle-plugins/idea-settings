package name.remal.gradleplugins.ideasettings.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.val;
import org.gradle.internal.os.OperatingSystem;
import org.junit.jupiter.api.Test;

class ConfigureExternalAnnotationsRootTest {

    @Test
    void doesFileUriStartWith_simple() {
        assertTrue(ConfigureExternalAnnotationsRoot.doesFileUriStartWith("file://C:/dir", "file://C:"));
        assertFalse(ConfigureExternalAnnotationsRoot.doesFileUriStartWith("file://C:/dir", "file://D:"));
    }

    @Test
    void doesFileUriStartWith_windows() {
        val result = ConfigureExternalAnnotationsRoot.doesFileUriStartWith("file://C:/dir", "file://c:");
        assertEquals(OperatingSystem.current().isWindows(), result);
    }

}
