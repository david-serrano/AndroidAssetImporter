import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;

@com.intellij.openapi.components.State(name = "PersistDialogService", storages = {@Storage(id = "default", file = "$PROJECT_FILE$"),
        @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/assetimportsettings.xml", scheme = StorageScheme.DIRECTORY_BASED)})
public class PersistDialogService implements PersistentStateComponent<PersistDialogService> {

    private boolean stateValue = true;

    @Override
    public PersistDialogService getState() {
        return this;
    }

    @Override
    public void loadState(PersistDialogService state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean getStateValue() {
        return this.stateValue;
    }

    public void setStateValue(boolean stateValue) {
        this.stateValue = stateValue;
    }
}