import java.io.File;

public class FileFinder {

    public boolean containsDpi(File root, String dpi) {
        if (root.getName().equals(dpi)) {
            return false;
        }

        File[] files = root.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.getName().contains(dpi)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String findDir(File root, String name) {
        if (root.getName().equals(name)) {
            return root.getAbsolutePath();
        }

        File[] files = root.listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    String myResult = findDir(f, name);
                    if (myResult != null) {
                        return myResult;
                    }
                }
            }
        }

        return null;
    }

    public boolean isAndroidProject(File directory) {
        String androidProject = findDir(directory, "app");
        String gradle = findDir(directory, "gradle");
        return androidProject != null && gradle != null;
    }
}
