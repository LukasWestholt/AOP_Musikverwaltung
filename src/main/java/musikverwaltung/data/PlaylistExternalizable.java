package musikverwaltung.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import javafx.scene.image.Image;
import musikverwaltung.Helper;

/**
 * Allows the Playlist object to be externalized and saved as part of SettingsFile
 */
public class PlaylistExternalizable implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 20L;

    private String name;

    private final ArrayList<String> songs = new ArrayList<>();
    private String previewImagePath;

    /**
     * changes the attributes of playlist to the only important information that need to be saved
     * name property -> only the string
     * all songs -> only the paths of every song
     * cover image object -> only the image url
     *
     * @param playlist playlist object that will be externalized
     */
    public PlaylistExternalizable(Playlist playlist) {
        name = playlist.getName();
        for (Song song : playlist.getAll()) {
            songs.add(Helper.p2uris(song.getPath()));
        }
        Image image = playlist.getPreviewImage();
        if (image == null) {
            previewImagePath = "";
        } else {
            previewImagePath = image.getUrl();
        }
    }

    /**
     * Externalizable needs a public no-args constructor
     */
    public PlaylistExternalizable() {}

    /**
     * @return name of Playlist
     */
    public String getName() {
        return name;
    }

    /**
     * @param name = name of Playlist
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return path of preview image for the playlist
     */
    public String getPreviewImagePath() {
        return previewImagePath;
    }

    /**
     * @param path = path of preview image for the playlist
     */
    public void setPreviewImagePath(String path) {
        this.previewImagePath = path;
    }

    /**
     * @return the paths of every Song in the Playlist in an ArrayList
     */
    public ArrayList<String> getPaths() {
        return songs;
    }

    /**
     * @param songs = the paths of every Song in the Playlist in an ArrayList
     */
    public void addPaths(ArrayList<String> songs) {
        this.songs.addAll(songs);
    }

    /**
     * overrides the writeExternal method of Externalizable
     * externalizes playlist object (name, song paths, image path)
     *
     * @param out the stream to write the object to
     * @throws IOException
     */
    //https://www.geeksforgeeks.org/externalizable-interface-java/
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeObject(songs);
        out.writeUTF(previewImagePath);
    }

    /**
     * overrides the readExternal method of Externalizable
     * reads in externalizes playlist object (name, song paths, image path)
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException, ClassNotFoundException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName(in.readUTF());
        addPaths((ArrayList<String>) in.readObject());
        setPreviewImagePath(in.readUTF());
    }
}
