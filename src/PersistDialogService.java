import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Property;

@com.intellij.openapi.components.State(name = "showDialog", storages = {@Storage(id = "default", file = StoragePathMacros.PROJECT_FILE)})
public class PersistDialogService implements PersistentStateComponent<PersistDialogService> {

   /* public static class State {
        public boolean value;
    }*/

    // private State myState = new State();

    public boolean stateValue;

    @Override
    public PersistDialogService getState() {
        return this;
    }

    @Override
    public void loadState(PersistDialogService state) {
        XmlSerializerUtil.copyBean(state, this);
    }
/*
    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(State state) {
        myState = state;
    }

    public boolean getValue() {
        return myState.value;
    }

    public void addValue(boolean value) {
        myState.value = value;
    }*/
}