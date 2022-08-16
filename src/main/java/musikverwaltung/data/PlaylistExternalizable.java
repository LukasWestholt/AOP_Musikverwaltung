package musikverwaltung.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 * Allows the Playlist object to be externalized and saved as part of SettingsFile
 */
public class PlaylistExternalizable implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 20L;

    private String name;

    private final ArrayList<URIS> songs = new ArrayList<>();
    private URIS previewImagePath;

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
            songs.add(new URIS(song.getPath()));
        }
        previewImagePath = new URIS(playlist.getPreviewImage());
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
     * @param name name of Playlist
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return identifier for preview image
     */
    public URIS getPreviewImagePath() {
        return previewImagePath;
    }

    /**
     * @param uris identifier for preview image
     */
    public void setPreviewImagePath(URIS uris) {
        this.previewImagePath = uris;
    }

    /**
     * @return identifiers for all songs in PlaylistExternalizable
     */
    public ArrayList<URIS> getPaths() {
        return songs;
    }

    /**
     * @param songs adds all identifiers for songs in PlaylistExternalizable -> adds new songs
     */
    public void addPaths(ArrayList<URIS> songs) {
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
        out.writeObject(previewImagePath);
    }

    /**
     * overrides the readExternal method of Externalizable
     * reads in externalizes playlist object (name, song paths, image path)
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName(in.readUTF());
        addPaths((ArrayList<URIS>) in.readObject());
        setPreviewImagePath((URIS) in.readObject());
    }
}
