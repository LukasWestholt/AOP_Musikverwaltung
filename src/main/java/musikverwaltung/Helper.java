package musikverwaltung;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Helper class with static methods.
 */
public class Helper {


    public static List<String> imageExtensions = List.of(
            "*.png", "*.jpg", "*.jpeg", "*.tif", "*.tiff", "*.gif", "*.bmp"
    );
    public static String audioExtensions = "*.{wav,mp3,m4a,aif,aiff}";

    /**
     * get a resource path by a path string.
     * https://stackoverflow.com/a/19459180/8980073
     *
     * @param c The class to which the resources are searched relative to.
     * @param resourcePath The relative path of the resource.
     * @param exitOnFailure JVA terminates with error on failure if true.
     * @return Path from URL or null.
     */
    public static Path getResourcePath(Class<?> c, String resourcePath, boolean exitOnFailure) {
        if (!resourcePath.startsWith("/")) {
            System.out.println("resourcePath is not starting with a '/'. Are you sure this is correct?");
        }
        URL url = c.getResource(resourcePath);
        Path path = url2p(url);
        if (path == null & exitOnFailure) {
            System.out.println("Resource \"" + resourcePath + "\" not found. Aborting.");
            System.exit(-1);
        }
        return path;
    }

    public static String getResourcePathUriString(Class<?> c, String resourcePath, boolean exitOnFailure) {
        return p2uris(getResourcePath(c, resourcePath, exitOnFailure));
    }

    public static String p2uris(Path path) {
        if (path == null) {
            return "";
        }
        return path.toUri().toString();
    }

    public static Path s2p(String string) {
        return Paths.get(string);
    }

    public static Path uris2p(String string) {
        if (string.isEmpty()) {
            return null;
        }
        try {
            return Paths.get(new URI(string));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path url2p(URL url) {
        if (url != null) {
            try {
                return Paths.get(url.toURI());
            } catch (URISyntaxException e) {
                return Paths.get(url.getPath());
            }
        }
        return null;
    }
}
