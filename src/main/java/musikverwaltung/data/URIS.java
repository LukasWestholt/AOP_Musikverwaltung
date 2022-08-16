package musikverwaltung.data;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

/**
 * URL string.
 */
public class URIS implements Serializable {
    private final String underlyingString;

    public URIS(Path path) {
        if (path == null) {
            this.underlyingString = "";
        } else {
            this.underlyingString = path.toUri().toString();
        }
    }

    public URIS(Image image) {
        if (image == null) {
            this.underlyingString = "";
        } else {
            this.underlyingString = image.getUrl();
        }
    }

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

    public Image toImage(boolean backgroundLoading) {
        return new Image(underlyingString, backgroundLoading);
    }

    public Media toMedia() {
        return new Media(underlyingString);
    }

    @Override
    public String toString() {
        return underlyingString;
    }
}
