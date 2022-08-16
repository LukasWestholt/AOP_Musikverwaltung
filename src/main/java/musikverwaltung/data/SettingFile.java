package musikverwaltung.data;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import javafx.collections.ObservableList;
import musikverwaltung.Helper;


/**
 * externalizable object that saves the settings of the Musikverwaltung
 * it holds the name of the file all information gets saved to
 * information about the directories where the audio files from the user are located -> all songs will be
 * visible as long as the directory paths are the same
 * information about the last played song -> can be seen in Player next time the app is opened
 * information about all playlists -> can be seen in PlaylistView next time the app is opened
 */
public class SettingFile implements Externalizable {
    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 10L;
    private static final String filename = "settings.ser";
    private ArrayList<PlaylistExternalizable> playlists = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private Path lastSong;
    private boolean showUnplayableSongs;

    /**
     * creates array of PlaylistExternalizable and compares it to the already saved playlists
     * if there are the same, nothing happens, else: new playlist will be saved as part of SettingsFile
     *
     * @param playlists = playlists from the user
     */
    public static void savePlaylists(ObservableList<Playlist> playlists) {
        SettingFile setting = load();
        ArrayList<PlaylistExternalizable> playlistsExt = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistsExt.add(new PlaylistExternalizable(playlist));
        }
        if (!setting.playlists.equals(playlistsExt)) {
            setting.playlists = playlistsExt;
            save(setting);
            System.out.println("added playlists to SettingFile " + playlists);
        }
    }

    /**
     * compares already saved paths with paths
     * if there are the same, nothing happens, else: new paths will be saved as part of SettingsFile
     *
     * @param paths = paths to the directories where the audio files from the user are located
     */
    public static void savePaths(ArrayList<String> paths) {
        SettingFile setting = load();
        if (!setting.paths.equals(paths)) {
            setting.paths = paths;
            save(setting);
            System.out.println("added paths to SettingFile " + paths);
        }
    }

    /**
     * compares already saved last played Song with lastSong
     * if there are the same, nothing happens, else: new lastSong will be saved as part of SettingsFile
     *
     * @param lastSong = last played Song
     */
    public static void saveLastSong(Path lastSong) {
        SettingFile setting = load();
        if (!Objects.equals(setting.lastSong, lastSong)) {
            setting.lastSong = lastSong;
            save(setting);
            System.out.println("added lastSong to SettingFile " + lastSong);
        }
    }

    /**
     * compares already saved unplayable songs with UnplayableSong
     * if there are the same, nothing happens, else: new UnplayableSong will be saved as part of SettingsFile
     *
     * @param showUnplayableSongs = information whether unplayable songs
     *                            (wrong path or wrong format) should be saved too
     */
    public static void saveShowUnplayableSongs(boolean showUnplayableSongs) {
        SettingFile setting = load();
        if (setting.showUnplayableSongs != showUnplayableSongs) {
            setting.showUnplayableSongs = showUnplayableSongs;
            save(setting);
            System.out.println("added showUnplayableSongs to SettingFile " + showUnplayableSongs);
        }
    }

    /**
     * @return saved playlists
     */
    public ArrayList<PlaylistExternalizable> getPlaylists() {
        return playlists;
    }

    /**
     * @return saved directory paths
     */
    public ArrayList<String> getPaths() {
        return paths;
    }

    /**
     * @return saved last Song
     */
    public Path getLastSong() {
        return lastSong;
    }

    /**
     * @return information whether unplayable songs are included
     */
    public boolean getShowUnplayableSongs() {
        return showUnplayableSongs;
    }

    /**
     * @return gets saved SettingFile object from file it was saved to
     */
    public static SettingFile load() {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream s = new ObjectInputStream(in);
            return (SettingFile) s.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException ignored) {
            return new SettingFile();
        }
    }

    /**
     * @param setting SettingsFile object that will be written to a file
     */
    private static void save(SettingFile setting) {
        try (
                FileOutputStream f = new FileOutputStream(filename);
                ObjectOutputStream s = new ObjectOutputStream(f)
        ) {
            s.writeObject(setting);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }
    /**
     * overrides the writeExternal method of Externalizable
     * externalizes SettingsFile object (playlists, paths, lastSong, showUnplayableSongs)
     *
     * @param out the stream to write the object to
     * @throws IOException
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(playlists);
        out.writeObject(paths);
        out.writeObject(new URIS(lastSong));
        out.writeBoolean(showUnplayableSongs);
    }

    /**
     * overrides the readExternal method of Externalizable
     * reads in externalizes playlist object (playlists, paths, lastSong, showUnplayableSongs)
     *
     * @param in the stream to read data from in order to restore the object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.playlists = (ArrayList<PlaylistExternalizable>) in.readObject();
        this.paths = (ArrayList<String>) in.readObject();
        this.lastSong = ((URIS) in.readObject()).getPath();
        this.showUnplayableSongs = in.readBoolean();
    }

    /**
     * @return String representation of SettingsFile object
     */
    @Override
    public String toString() {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("playlists", getPlaylists());
        attributes.put("paths", getPaths());
        attributes.put("lastSong", getLastSong());
        attributes.put("showUnplayableSongs", getShowUnplayableSongs());
        return Helper.toString(this, attributes);
    }
}
