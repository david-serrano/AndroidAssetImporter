import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class InfoDialog extends AnAction {

    public InfoDialog() {
        super();
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PersistDialogService persisted = new PersistDialogService();
        boolean showDialog = persisted.getState().stateValue;
        if (!showDialog) {
            if (Messages.showCheckboxMessageDialog("Hi and welcome to the Asset Importer for Android Studio, " +
                            "\n\nPlease note that all the files must be of type .png and contain the density specification within their file name. " +
                            "\n\nFor example myNewAsset-mdpi.png", "Information",
                    new String[]{"OK"}, "Don't show again", false, -1, -1, Messages.getInformationIcon(), null) == 0) {
                persisted.getState().stateValue = true;
                persisted.loadState(persisted);
            }
        }

        new Main(event);
    }
}