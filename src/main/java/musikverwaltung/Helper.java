package musikverwaltung;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Helper class with static methods.
 */
public class Helper {
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
        Path path = getPath(url);
        if (path == null & exitOnFailure) {
            System.out.println("Resource \"" + resourcePath + "\" not found. Aborting.");
            System.exit(-1);
        }
        return path;
    }

    public static String getResourcePathString(Class<?> c, String resourcePath, boolean exitOnFailure) {
        return p2s(getResourcePath(c, resourcePath, exitOnFailure));
    }

    public static String p2s(Path path) {
        if (path == null) {
            return "";
        }
        return path.toUri().toString();
    }

    public static Path getPath(String string) {
        return Paths.get(string);
    }

    public static Path getPath(URL url) {
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
