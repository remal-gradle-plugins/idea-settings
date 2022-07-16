package name.remal.gradleplugins.ideasettings;

import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Data;

@Data
public class IdeaDatabaseSettings {

    @Nullable
    private String defaultDialect;


    @Inject
    public IdeaDatabaseSettings() {
        // do nothing
    }
}
