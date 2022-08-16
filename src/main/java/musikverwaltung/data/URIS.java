package musikverwaltung.data;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

/**
 * URI string data structure
 */
public class URIS implements Serializable {
    private final String underlyingString;


    /**
     * Constructs a URIS based on a Path
     *
     * @param path a Path object
     */
    public URIS(Path path) {
        if (path == null) {
            this.underlyingString = "";
        } else {
            this.underlyingString = path.toUri().toString();
        }
    }

    /**
     * Constructs a URIS based on a Image
     *
     * @param image an Image object
     */
    public URIS(Image image) {
        if (image == null) {
            this.underlyingString = "";
        } else {
            this.underlyingString = image.getUrl();
        }
    }


    /**
     * Convert to Path
     *
     * @return a Path object
     */
    public Path toPath() {
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
     * Convert to Image
     *
     * @param backgroundLoading indicates whether the image is being loaded in the background
     * @return an Image object
     */
    public Image toImage(boolean backgroundLoading) {
        return new Image(underlyingString, backgroundLoading);
    }

    /**
     * Convert to Media
     *
     * @return an Media object
     */
    public Media toMedia() {
        return new Media(underlyingString);
    }

    @Override
    public String toString() {
        return underlyingString;
    }
}
