package musikverwaltung;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Helper class with static methods.
 */
public class Helper {
    /**
     * get a resource file by a path.
     * https://stackoverflow.com/a/19459180/8980073
     *
     * @param c The class to which the resources are searched relative to.
     * @param resourcePath The relative path of the resource.
     * @param exitOnFailure JVA terminates with error on failure if true.
     * @return File from URL.
     */
    public static File getResourceFile(Class<?> c, String resourcePath, boolean exitOnFailure) {
        URL url = c.getResource(resourcePath);
        if (url != null) {
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                return new File(url.getPath());
            }
        }
        if (exitOnFailure) {
            System.out.println("Resource \"" + resourcePath + "\" not found. Aborting.");
            System.exit(-1);
        }
        return new File("");
    }
}
