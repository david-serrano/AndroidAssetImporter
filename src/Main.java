import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main implements MainLayout.MenuInterface, FileChooser.ChooserCallback {
    private static final String RES_PATH = "/app/src/main/res";


    private static final String MDPI = "mdpi";
    private static final String XXXHDPI = "xxxhdpi";
    private static final String XXHDPI = "xxhdpi";
    private static final String XHDPI = "xhdpi";
    private static final String HDPI = "hdpi";

    private MainLayout mainLayout;
    private boolean mdpiSelected = false;
    private boolean hdpiSelected = false;
    private boolean xhdpiSelected = false;
    private boolean xxhdpiSelected = false;
    private boolean xxxhdpiSelected = false;
    private int dpiCount = 5;
    private boolean assetDirectorySet = false;
    private FileChooser fileChooser = new FileChooser();
    private Project project;
    private File assetDirectory;
    private File projectDirectory;
    private JFrame frame;
    private JTextField assetName;
    private boolean validAssetName = false;
    private String assetExtension;
    private FileFinder finder = new FileFinder();
    private Color errorColor = new Color(187, 54, 58);
    private Color successColor = new Color(84, 187, 68);

    enum ChoiceType {
        ASSET,
        PROJECT
    }

    public Main(AnActionEvent e) {
        project = e.getData(PlatformDataKeys.PROJECT);
        mainLayout = new MainLayout();
        frame = new JFrame("Android Asset Importer");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setContentPane(mainLayout.getMainPanel());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
        MainLayout.MenuInterface menuInterface = this;
        mainLayout.setupToolbar(menuInterface);

        initLayout();

        frame.pack();
        frame.setFocusableWindowState(false);
        frame.setVisible(true);
        try {
            Thread.sleep(250);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        frame.setFocusableWindowState(true);
    }

    private void initLayout() {
        projectDirectory = new File(project.getBasePath());
        mainLayout.setLocationDirectory(projectDirectory.getAbsolutePath());
        if (!finder.isAndroidProject(projectDirectory)) {
            setErrorMessage("Current directory is not an Android project.");
        }
        assetName = mainLayout.getNameField();
        assetName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTextValidity(assetName.getText());
                setImportButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTextValidity(assetName.getText());
                setImportButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        setButtonListeners();

        setCheckBoxListeners();

        setExtensionListener();
    }

    @Override
    public void closeProgram() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void resetDefaults() {
        PersistDialogService persisted = ServiceManager.getService(project, PersistDialogService.class);
        persisted.setStateValue(true);
        persisted.loadState(persisted);
    }

    private void checkTextValidity(String text) {
        if (text.matches(".*[A-Z. ].*") || text.isEmpty()) {
            setErrorMessage("Invalid asset name.");
            validAssetName = false;
        } else {
            mainLayout.getMessageLabel().setText("");
            validAssetName = true;
        }
    }

    private void setExtensionListener() {
        ExtensionManager extensionManager = new ExtensionManager();
        assetExtension = extensionManager.manageBox(mainLayout.getAssetFileExtension());
    }

    @Override
    public void setErrorMessage(String message) {
        mainLayout.getMessageLabel().setForeground(errorColor);
        mainLayout.getMessageLabel().setText(message);
        frame.pack();
    }

    @Override
    public void resetSelections() {
        mdpiSelected = false;
        hdpiSelected = false;
        xhdpiSelected = false;
        xxhdpiSelected = false;
        xxxhdpiSelected = false;

        mainLayout.getHdpiOption().setEnabled(hdpiSelected);
        mainLayout.getHdpiOption().setSelected(hdpiSelected);

        mainLayout.getMdpiOption().setEnabled(mdpiSelected);
        mainLayout.getMdpiOption().setSelected(mdpiSelected);

        mainLayout.getXhdpiOption().setEnabled(xhdpiSelected);
        mainLayout.getXhdpiOption().setSelected(xhdpiSelected);

        mainLayout.getXxhdpiOption().setEnabled(xxhdpiSelected);
        mainLayout.getXxhdpiOption().setSelected(xxhdpiSelected);

        mainLayout.getXxxhdpiOption().setEnabled(xxxhdpiSelected);
        mainLayout.getXxxhdpiOption().setSelected(xxxhdpiSelected);
    }

    private void resetUI() {
        resetSelections();
        assetName.setText("");
        assetName.setEnabled(false);
        setErrorMessage("");
        assetDirectory = null;
        mainLayout.setAssetFolderLabel("Not Set");
    }

    private boolean setCheckBoxState(boolean dpi) {
        if (dpi) {
            dpiCount--;
            dpi = false;
        } else {
            dpiCount++;
            dpi = true;
        }
        setImportButtonState();

        return dpi;
    }

    private String setDpiChecks(File root) {
        String highestResolution = "";
        boolean mdpiAsset = finder.containsDpi(root, MDPI);
        if (mdpiAsset) {
            mdpiSelected = setCheckBoxState(mdpiSelected);
            mainLayout.getMdpiOption().setEnabled(mdpiSelected);
            mainLayout.getMdpiOption().setSelected(mdpiSelected);
            highestResolution = MDPI;
        }
        boolean xhdpiAsset = finder.containsDpi(root, XHDPI);
        if (xhdpiAsset) {
            xhdpiSelected = setCheckBoxState(xhdpiSelected);
            mainLayout.getXhdpiOption().setEnabled(xhdpiSelected);
            mainLayout.getXhdpiOption().setSelected(xhdpiSelected);
            highestResolution = XHDPI;
        }
        boolean xxhdpiAsset = finder.containsDpi(root, XXHDPI);
        if (xxhdpiAsset) {
            xxhdpiSelected = setCheckBoxState(xxxhdpiSelected);
            mainLayout.getXxhdpiOption().setEnabled(xxhdpiSelected);
            mainLayout.getXxhdpiOption().setSelected(xxhdpiSelected);
            highestResolution = XXHDPI;
        }
        boolean xxxhdpiAsset = finder.containsDpi(root, XXXHDPI);
        if (xxxhdpiAsset) {
            xxxhdpiSelected = setCheckBoxState(xxxhdpiSelected);
            mainLayout.getXxxhdpiOption().setEnabled(xxxhdpiSelected);
            mainLayout.getXxxhdpiOption().setSelected(xxxhdpiSelected);
            highestResolution = XXXHDPI;
        }
        boolean hdpiAsset = finder.containsDpi(root, HDPI);
        if (hdpiAsset) {
            hdpiSelected = setCheckBoxState(hdpiSelected);
            mainLayout.getHdpiOption().setEnabled(hdpiSelected);
            mainLayout.getHdpiOption().setSelected(hdpiSelected);
            highestResolution = HDPI;
        }
        return highestResolution;
    }

    private void setCheckBoxListeners() {
        ActionListener mdpiListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mdpiSelected = setCheckBoxState(mdpiSelected);
            }
        };
        mainLayout.setMdpiOptionListener(mdpiListener);

        ActionListener hdpiListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hdpiSelected = setCheckBoxState(hdpiSelected);
            }
        };
        mainLayout.setHdpiOptionListener(hdpiListener);

        ActionListener xhdpiListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xhdpiSelected = setCheckBoxState(xhdpiSelected);
            }
        };
        mainLayout.setXhdpiOptionListener(xhdpiListener);

        ActionListener XxhdpiListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xxhdpiSelected = setCheckBoxState(xxhdpiSelected);
            }
        };
        mainLayout.setXxhdpiOptionListener(XxhdpiListener);

        ActionListener xxxhdpiListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xxxhdpiSelected = setCheckBoxState(xxxhdpiSelected);
            }
        };
        mainLayout.setXxxhdpiOptionListener(xxxhdpiListener);
    }

    private void setImportButtonState() {
        if (dpiCount > 0 && assetDirectorySet && validAssetName) {
            mainLayout.getImportButton().setEnabled(true);
        } else {
            mainLayout.getImportButton().setEnabled(false);
        }
    }

    @Override
    public void prepareAssets(File selectedFile) {
        assetDirectorySet = true;
        assetDirectory = selectedFile;
        String resolution = setDpiChecks(assetDirectory);
        if (!resolution.isEmpty()) {
            ArrayList<Asset> assets = new ArrayList<>();
            collectAssets(assets);
            for (Asset a : assets) {
                if (a.getType().toString().toLowerCase().equals(resolution)) {
                    File img = a.getFile();
                    try {
                        BufferedImage buffImg = ImageIO.read(img);
                        ImageIcon icon = new ImageIcon(buffImg);
                        mainLayout.getImagePreviewLabel().setText("");
                        mainLayout.getImagePreviewLabel().setIcon(icon);
                    } catch (IOException e) {
                        mainLayout.getImagePreviewLabel().setText("Unable to load preview.");
                        e.printStackTrace();
                    }
                }
            }
        }
        mainLayout.getNameField().setEnabled(true);
        mainLayout.setAssetFolderLabel(assetDirectory.toString());
        setImportButtonState();
        frame.pack();
    }

    @Override
    public void setProjectDirectory(File directory) {
        projectDirectory = directory;
    }

    private void setButtonListeners() {
        FileChooser.ChooserCallback callback = this;
        ActionListener folderChooserListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.getChooser().setCurrentDirectory(projectDirectory);
                fileChooser.getChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showChooser(ChoiceType.PROJECT, mainLayout, callback, frame);
            }
        };
        mainLayout.setDirectoryOnClick(folderChooserListener);

        ActionListener assetChooserListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fileChooser.getChooser().setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.getChooser().setDialogTitle("Select Asset Folder");
                fileChooser.getChooser().setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.getChooser().setAcceptAllFileFilterUsed(false);

                fileChooser.showChooser(ChoiceType.ASSET, mainLayout, callback, frame);
            }

        };
        mainLayout.setAssetOnClick(assetChooserListener);

        ActionListener importListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Asset> assets = new ArrayList<>();
                collectAssets(assets);

                if (assets.size() > 0) {
                    writeFiles(assets);
                }
            }
        };
        mainLayout.setImportClick(importListener);
    }

    private void collectAssets(ArrayList<Asset> assets) {
        File[] directoryListing = assetDirectory.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                Asset tempFile = sortFile(child);
                if (tempFile != null) {
                    assets.add(tempFile);
                }
            }
        }
    }

    private void writeFiles(ArrayList<Asset> assets) {
        File app = new File(projectDirectory + RES_PATH);
        if (app.exists()) {
            for (Asset a : assets) {
                checkFolderExistsOrCreate(a, app);
            }
            mainLayout.getMessageLabel().setForeground(successColor);
            mainLayout.getMessageLabel().setText("All assets imported successfully.");
            if (Messages.showOkCancelDialog(project, "Asset import successful, do you want to import another asset?", "Import Successful", "Import another", "I'm done", Messages.getQuestionIcon()) == Messages.OK) {
                resetUI();
            } else {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            }

        } else {
            setErrorMessage("Invalid base directory.");
        }
    }

    private void checkFolderExistsOrCreate(Asset asset, File parent) {
        switch (asset.getType()) {
            case MDPI:
                File mdpi = new File(parent.getAbsolutePath() + "/drawable-mdpi");
                createFolderOrAsset(asset, mdpi);
                break;
            case HDPI:
                File hdpi = new File(parent.getAbsolutePath() + "/drawable-hdpi");
                createFolderOrAsset(asset, hdpi);
                break;
            case XHDPI:
                File xhdpi = new File(parent.getAbsolutePath() + "/drawable-xhdpi");
                createFolderOrAsset(asset, xhdpi);
                break;
            case XXHDPI:
                File xxhdpi = new File(parent.getAbsolutePath() + "/drawable-xxhdpi");
                createFolderOrAsset(asset, xxhdpi);
                break;
            case XXXHDPI:
                File xxxhdpi = new File(parent.getAbsolutePath() + "/drawable-xxxhdpi");
                createFolderOrAsset(asset, xxxhdpi);
                break;
        }
    }

    private void createFolderOrAsset(Asset asset, File file) {
        if (!file.exists()) {
            boolean created = file.mkdir();
            if (created) {
                createAsset(asset, file);
            }
        } else {
            createAsset(asset, file);
        }
    }

    private void createAsset(Asset asset, File file) {
        File f = new File(file.getAbsolutePath() + "/" + assetName.getText() + assetExtension);
        if (!f.exists()) {
            try {
                Files.copy(asset.getFile().toPath(), f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //asset already created
        }
    }

    private Asset sortFile(File child) {
        Asset asset;
        if (mdpiSelected && child.getName().contains(MDPI)) {
            asset = new Asset(child, Asset.Density.MDPI);
        } else if (xxxhdpiSelected && child.getName().contains(XXXHDPI)) {
            asset = new Asset(child, Asset.Density.XXXHDPI);
        } else if (xxhdpiSelected && child.getName().contains(XXHDPI)) {
            asset = new Asset(child, Asset.Density.XXHDPI);
        } else if (xhdpiSelected && child.getName().contains(XHDPI)) {
            asset = new Asset(child, Asset.Density.XHDPI);
        } else if (hdpiSelected && child.getName().contains(HDPI)) {
            asset = new Asset(child, Asset.Density.HDPI);
        } else {
            asset = null;
        }
        return asset;
    }
}
