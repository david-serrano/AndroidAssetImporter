import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

public class InfoDialog extends AnAction {

    public InfoDialog() {
        super();
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PersistDialogService persisted = ServiceManager.getService(project, PersistDialogService.class);
        boolean showDialog = persisted.getStateValue();
        if (showDialog) {
            if (showInfoDialog() == 0) {
                persisted.setStateValue(false);
                persisted.loadState(persisted);
            }
        }

        new Main(event);
    }

    private int showInfoDialog() {
        return Messages.showCheckboxMessageDialog("Hi and welcome to the Asset Importer for Android Studio!" +
                        "\n\n\nPlease note that all the files must be of type .png and contain the density specification within their file name. " +
                        "\n\n\nFor example my_new_asset-mdpi.png", "Information",
                new String[]{"OK"}, "Don't show again", false, -1, -1, Messages.getInformationIcon(), null);
    }
}