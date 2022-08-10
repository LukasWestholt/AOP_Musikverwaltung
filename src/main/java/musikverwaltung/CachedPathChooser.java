package musikverwaltung;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class CachedPathChooser {
    // https://stackoverflow.com/a/38033848/8980073
    private static FileChooser fileChooser = null;
    private static DirectoryChooser directoryChooser = null;
    private static final SimpleObjectProperty<File> lastKnownDirectoryProperty = new SimpleObjectProperty<>();
    private static final SimpleStringProperty titleProperty = new SimpleStringProperty();

    private static FileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new FileChooser();
            fileChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
            fileChooser.titleProperty().bindBidirectional(titleProperty);
            //Set the FileExtensions you want to allow
            //fileChooser.getExtensionFilters().setAll(new ExtensionFilter("png files (*.png)", "*.png"));
        }
        validateDirectory();
        return fileChooser;
    }

    private static DirectoryChooser getDirectoryChooser() {
        if (directoryChooser == null) {
            directoryChooser = new DirectoryChooser();
            directoryChooser.initialDirectoryProperty().bindBidirectional(lastKnownDirectoryProperty);
            directoryChooser.titleProperty().bindBidirectional(titleProperty);
        }
        validateDirectory();
        return directoryChooser;
    }

    public static Path showOpenDialog(Window ownerWindow, String title) {
        titleProperty.set(title);
        return handleChosenFile(getFileChooser().showOpenDialog(ownerWindow));
    }

    public static Path showSaveDialog(Window ownerWindow, String title) {
        titleProperty.set(title);
        return handleChosenFile(getFileChooser().showSaveDialog(ownerWindow));
    }

    public static Path showDialog(Window ownerWindow, String title) {
        titleProperty.set(title);
        return handleChosenFile(getDirectoryChooser().showDialog(ownerWindow));
    }

    private static Path handleChosenFile(File chosenFile) {
        if (chosenFile != null) {
            lastKnownDirectoryProperty.setValue(chosenFile.getParentFile());
            try {
                return chosenFile.toPath();
            } catch (InvalidPathException ignored) {
                System.out.println("Problem on loading File");
            }
        }
        return null;
    }

    private static void validateDirectory() {
        if (lastKnownDirectoryProperty.get() == null || !lastKnownDirectoryProperty.get().exists()) {
            lastKnownDirectoryProperty.set(new File(System.getProperty("user.home")));
            if (lastKnownDirectoryProperty.get() != null && !lastKnownDirectoryProperty.get().exists()) {
                lastKnownDirectoryProperty.set(null);
            }
        }
    }
}
