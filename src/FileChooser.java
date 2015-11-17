import javax.swing.*;
import java.io.File;

public class FileChooser {

    public interface ChooserCallback {
        void resetSelections();

        void prepareAssets(File selectedFile);

        void setErrorMessage(String message);

        void setProjectDirectory(File directory);
    }

    private FileFinder finder = new FileFinder();
    private JFileChooser chooser = new JFileChooser() {
        public void approveSelection() {
            if (getSelectedFile().isFile()) {
                return;
            } else
                super.approveSelection();
        }
    };

    public JFileChooser getChooser() {
        return chooser;
    }

    public void showChooser(Main.ChoiceType type, MainLayout mainLayout, ChooserCallback listener, JFrame frame) {
        mainLayout.getMessageLabel().setText("");
        if (chooser.showOpenDialog(mainLayout.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
            if (type == Main.ChoiceType.ASSET) {
                listener.resetSelections();
                listener.prepareAssets(chooser.getSelectedFile());
            } else if (type == Main.ChoiceType.PROJECT) {
                File tempDirectory = chooser.getSelectedFile();
                if (finder.isAndroidProject(tempDirectory)) {
                    listener.setProjectDirectory(tempDirectory);
                    mainLayout.setLocationDirectory(tempDirectory.toString());
                } else {
                    listener.setErrorMessage("Selected directory is not an Android project.");
                }
            }
            frame.pack();
        } else {
            //no selection
        }
    }
}
