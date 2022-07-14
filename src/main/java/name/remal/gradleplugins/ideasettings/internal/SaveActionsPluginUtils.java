package name.remal.gradleplugins.ideasettings.internal;

import static java.beans.Introspector.getBeanInfo;
import static lombok.AccessLevel.PRIVATE;
import static name.remal.gradleplugins.toolkit.reflection.ReflectionUtils.isPublic;

import java.util.Collection;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import name.remal.gradleplugins.ideasettings.IdeaRunOnSaveSettings;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
@NoArgsConstructor(access = PRIVATE)
abstract class SaveActionsPluginUtils {

    @SneakyThrows
    public static boolean isSaveActionsPluginConfigured(IdeaRunOnSaveSettings settings) {
        for (val prop : getBeanInfo(IdeaRunOnSaveSettings.class).getPropertyDescriptors()) {
            val readMethod = prop.getReadMethod();
            if (readMethod == null || !isPublic(readMethod) || readMethod.getDeclaringClass() == Object.class) {
                continue;
            }

            val value = readMethod.invoke(settings);
            if (value instanceof Collection) {
                if (!((Collection<?>) value).isEmpty()) {
                    return true;
                }
            } else if (value != null) {
                return true;
            }
        }

        return false;
    }

}
