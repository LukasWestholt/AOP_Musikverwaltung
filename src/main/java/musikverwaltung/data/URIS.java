package musikverwaltung.data;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

/**
 * Representation of customized URI - String
 */
public class URIS implements Serializable {
    private final String underlyingString;

    /**
     * created with Path
     *
     * @param path = path of image, file ...
     */
    public URIS(Path path) {
        if (path == null) {
            this.underlyingString = "";
        } else {
            this.underlyingString = path.toUri().toString();
        }
    }

    /**
     * created with image
     *
     * @param image = image
     */
    public URIS(Image image) {
        if (image == null) {
            this.underlyingString = "";
        } else {
            this.underlyingString = image.getUrl();
        }
    }

    /**
     * @return path as Path object if path doesn't contain error
     */
    public Path getPath() {
        if (underlyingString.isEmpty()) {
            return null;
        }
        try {
            return Paths.get(new URI(underlyingString));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param backgroundLoading = Image should be loaded in the background
     * @return as Image object
     */
    public Image toImage(boolean backgroundLoading) {
        return new Image(underlyingString, backgroundLoading);
    }

    /**
     * @return as Media object
     */
    public Media toMedia() {
        return new Media(underlyingString);
    }

    /**
     * @return as String Representation of URIS
     */
    @Override
    public String toString() {
        return underlyingString;
    }
}
