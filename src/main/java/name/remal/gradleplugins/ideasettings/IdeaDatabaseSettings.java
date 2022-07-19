package name.remal.gradleplugins.ideasettings;

import javax.annotation.Nullable;
import lombok.Data;

@Data
public class IdeaDatabaseSettings {

    @Nullable
    private String defaultDialect;


    public IdeaDatabaseSettings() {
        // do nothing
    }
}
