import javax.swing.*;
import java.awt.event.ActionListener;

public class MainLayout {
    private JLabel workingDirLabel;
    private JLabel workingDirectoryLocationLabel;
    private JPanel mainPanel;
    private JLabel assetNameLabel;
    private JLabel assetFolderLabel;
    private JLabel resolutionsLabel;

    private JButton selectWorkingDirectoryButton;
    private JButton selectAssetFolderButton;
    private JButton importButton;

    private JCheckBox mdpiOption;

    private JCheckBox hdpiOption;
    private JCheckBox xhdpiOption;
    private JCheckBox xxhdpiOption;
    private JCheckBox xxxhdpiOption;
    private JTextField assetNameField;
    private JComboBox assetFileExtension;
    private JLabel assetName;
    private JLabel messageLabel;

    public void setLocationDirectory(String dir) {
        workingDirectoryLocationLabel.setText(dir);
    }

    public JTextField getNameField() {
        return assetNameField;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void setAssetFolderLabel(String label) {
        assetNameLabel.setText(label);
    }

    public void setDirectoryOnClick(ActionListener actionListener) {
        selectWorkingDirectoryButton.addActionListener(actionListener);
    }

    public void setAssetOnClick(ActionListener actionListener) {
        selectAssetFolderButton.addActionListener(actionListener);
    }

    public void setImportClick(ActionListener actionListener) {
        importButton.addActionListener(actionListener);
    }

    public void setMdpiOptionListener(ActionListener listener) {
        mdpiOption.addActionListener(listener);
    }

    public void setHdpiOptionListener(ActionListener listener) {
        hdpiOption.addActionListener(listener);
    }

    public void setXhdpiOptionListener(ActionListener listener) {
        xhdpiOption.addActionListener(listener);
    }

    public void setXxhdpiOptionListener(ActionListener listener) {
        xxhdpiOption.addActionListener(listener);
    }

    public void setXxxhdpiOptionListener(ActionListener listener) {
        xxxhdpiOption.addActionListener(listener);
    }

    public JCheckBox getMdpiOption() {
        return mdpiOption;
    }

    public JCheckBox getHdpiOption() {
        return hdpiOption;
    }

    public JCheckBox getXhdpiOption() {
        return xhdpiOption;
    }

    public JCheckBox getXxhdpiOption() {
        return xxhdpiOption;
    }

    public JCheckBox getXxxhdpiOption() {
        return xxxhdpiOption;
    }

    public JButton getImportButton() {
        return importButton;
    }

    public JComboBox getAssetFileExtension() {
        return assetFileExtension;
    }

    public JLabel getMessageLabel() {
        return messageLabel;
    }
}
