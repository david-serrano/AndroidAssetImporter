import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main implements MainLayout.MenuInterface {
    private static final String RES_PATH = "/app/src/main/res";
    private static final String EXT_PNG = ".png";
    private static final String EXT_JPG = ".jpg";
    private static final String EXT_BMP = ".bmp";
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
    private JFileChooser chooser = new JFileChooser() {
        public void approveSelection() {
            if (getSelectedFile().isFile()) {
                return;
            } else
                super.approveSelection();
        }
    };
    ;
    private Project project;
    private File assetDirectory;
    private File projectDirectory;
    private JFrame frame;
    private JTextField assetName;
    private boolean validAssetName = false;
    private String assetExtension = EXT_PNG;
    private FileFinder finder = new FileFinder();
    private Color errorColor = new Color(187, 54, 58);
    private Color successColor = new Color(84, 187, 68);

    enum ChoiceType {
        ASSET,
        PROJECT;
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
        if (!isAndroidProject(projectDirectory)) {
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
        JComboBox extension = mainLayout.getAssetFileExtension();
        extension.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (extension.getSelectedIndex() == 0) {
                    assetExtension = EXT_PNG;
                } else if (extension.getSelectedIndex() == 1) {
                    assetExtension = EXT_JPG;
                } else if (extension.getSelectedIndex() == 2) {
                    assetExtension = EXT_BMP;
                }
            }
        });
    }

    private void setErrorMessage(String message) {
        mainLayout.getMessageLabel().setForeground(errorColor);
        mainLayout.getMessageLabel().setText(message);
        frame.pack();
    }

    private void resetSelections() {
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

    private void setDpiChecks(File root) {
        boolean mdpiAsset = finder.containsDpi(root, MDPI);
        if (mdpiAsset) {
            mdpiSelected = setCheckBoxState(mdpiSelected);
            mainLayout.getMdpiOption().setEnabled(mdpiSelected);
            mainLayout.getMdpiOption().setSelected(mdpiSelected);
        }
        boolean xhdpiAsset = finder.containsDpi(root, XHDPI);
        if (xhdpiAsset) {
            xhdpiSelected = setCheckBoxState(xhdpiSelected);
            mainLayout.getXhdpiOption().setEnabled(xhdpiSelected);
            mainLayout.getXhdpiOption().setSelected(xhdpiSelected);
        }
        boolean xxhdpiAsset = finder.containsDpi(root, XXHDPI);
        if (xxhdpiAsset) {
            xxhdpiSelected = setCheckBoxState(xxxhdpiSelected);
            mainLayout.getXxhdpiOption().setEnabled(xxhdpiSelected);
            mainLayout.getXxhdpiOption().setSelected(xxhdpiSelected);
        }
        boolean xxxhdpiAsset = finder.containsDpi(root, XXXHDPI);
        if (xxxhdpiAsset) {
            xxxhdpiSelected = setCheckBoxState(xxxhdpiSelected);
            mainLayout.getXxxhdpiOption().setEnabled(xxxhdpiSelected);
            mainLayout.getXxxhdpiOption().setSelected(xxxhdpiSelected);
        }
        boolean hdpiAsset = finder.containsDpi(root, HDPI);
        if (hdpiAsset) {
            hdpiSelected = setCheckBoxState(hdpiSelected);
            mainLayout.getHdpiOption().setEnabled(hdpiSelected);
            mainLayout.getHdpiOption().setSelected(hdpiSelected);
        }
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

    private void showChooser(ChoiceType type) {
        mainLayout.getMessageLabel().setText("");
        if (chooser.showOpenDialog(mainLayout.getMainPanel()) == JFileChooser.APPROVE_OPTION) {
            if (type == ChoiceType.ASSET) {
                resetSelections();
                prepareAssets();
            } else if (type == ChoiceType.PROJECT) {
                File tempDirectory = chooser.getSelectedFile();
                if (isAndroidProject(tempDirectory)) {
                    projectDirectory = tempDirectory;
                    mainLayout.setLocationDirectory(projectDirectory.toString());
                } else {
                    setErrorMessage("Selected directory is not an Android project.");
                }
            }
            frame.pack();
        } else {
            System.out.println("No Selection");
        }
    }

    private boolean isAndroidProject(File directory) {
        String androidProject = finder.findDir(directory, "app");
        String gradle = finder.findDir(directory, "gradle");
        return androidProject != null && gradle != null;
    }

    private void prepareAssets() {
        assetDirectorySet = true;
        assetDirectory = chooser.getSelectedFile();
        setDpiChecks(assetDirectory);
        mainLayout.getNameField().setEnabled(true);
        mainLayout.setAssetFolderLabel(assetDirectory.toString());
        setImportButtonState();
        frame.pack();
    }

    private void setButtonListeners() {
        ActionListener folderChooserListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooser.setCurrentDirectory(projectDirectory);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                showChooser(ChoiceType.PROJECT);
            }
        };
        mainLayout.setDirectoryOnClick(folderChooserListener);

        ActionListener assetChooserListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                chooser.setDialogTitle("Select Asset Folder");
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                chooser.setAcceptAllFileFilterUsed(false);

                showChooser(ChoiceType.ASSET);
            }

        };
        mainLayout.setAssetOnClick(assetChooserListener);

        ActionListener importListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File[] directoryListing = assetDirectory.listFiles();
                ArrayList<Asset> assets = new ArrayList<>();
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        Asset tempFile = sortFile(child);
                        if (tempFile != null) {
                            assets.add(tempFile);
                        }
                    }
                    //  System.out.println(String.format("found %d valid files in this directory", assets.size()));
                    if (assets.size() > 0) {
                        writeFiles(assets);
                    }
                }
            }
        };
        mainLayout.setImportClick(importListener);
    }

    private void writeFiles(ArrayList<Asset> assets) {
        //  System.out.println("base path is: " + projectDirectory.getAbsolutePath());

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
            //   System.out.println("folder exists");
            createAsset(asset, file);
        }
    }

    private void createAsset(Asset asset, File file) {
        // System.out.println("Asset path is: " + asset.getFile().getAbsolutePath());
        File f = new File(file.getAbsolutePath() + "/" + assetName.getText() + assetExtension);
        //  System.out.println("new file path = " + file.getAbsolutePath() + "file name = " + asset.getFile().getName());
        //   System.out.println("file path is: " + f.getAbsolutePath());
        if (!f.exists()) {
            try {
                Files.copy(asset.getFile().toPath(), f.toPath());
                //          System.out.println("file asset created successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //      System.out.println("Asset already created");
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
