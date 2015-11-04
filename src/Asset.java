import java.io.File;

public class Asset {

    private File file;
    private Asset.Density type;

    public Asset(File file, Density type) {
        this.file = file;
        this.type = type;
    }

    public Density getType() {
        return type;
    }

    public File getFile() {
        return file;
    }

    public enum Density {
        MDPI,
        HDPI,
        XHDPI,
        XXHDPI,
        XXXHDPI
    }
}
