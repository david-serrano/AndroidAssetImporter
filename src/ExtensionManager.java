import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExtensionManager {

    private static final String EXT_PNG = ".png";
    private static final String EXT_JPG = ".jpg";
    private static final String EXT_BMP = ".bmp";
    private static String extension;

    public String manageBox(JComboBox assetFileExtension) {
        extension = EXT_JPG;
        assetFileExtension.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (assetFileExtension.getSelectedIndex() == 0) {
                    extension = EXT_PNG;
                } else if (assetFileExtension.getSelectedIndex() == 1) {
                    extension = EXT_JPG;
                } else if (assetFileExtension.getSelectedIndex() == 2) {
                    extension = EXT_BMP;
                }
            }
        });
        return extension;
    }
}
