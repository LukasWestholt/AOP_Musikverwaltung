package musikverwaltung.data;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import javafx.collections.ObservableList;
import musikverwaltung.Helper;

public class SettingFile implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 10L;

    private static final String filename = "settings.ser";

    private ArrayList<PlaylistExternalizable> playlists = new ArrayList<>();
    private ArrayList<String> paths = new ArrayList<>();
    private Path lastSong;
    private boolean showUnplayableSongs;

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

    public static void savePaths(ArrayList<String> paths) {
        SettingFile setting = load();
        if (!setting.paths.equals(paths)) {
            setting.paths = paths;
            save(setting);
            System.out.println("added paths to SettingFile " + paths);
        }
    }

    public static void saveLastSong(Path lastSong) {
        SettingFile setting = load();
        if (!Objects.equals(setting.lastSong, lastSong)) {
            setting.lastSong = lastSong;
            save(setting);
            System.out.println("added lastSong to SettingFile " + lastSong);
        }
    }

    public static void saveShowUnplayableSongs(boolean showUnplayableSongs) {
        SettingFile setting = load();
        if (setting.showUnplayableSongs != showUnplayableSongs) {
            setting.showUnplayableSongs = showUnplayableSongs;
            save(setting);
            System.out.println("added showUnplayableSongs to SettingFile " + showUnplayableSongs);
        }
    }

    public ArrayList<PlaylistExternalizable> getPlaylists() {
        return playlists;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public Path getLastSong() {
        return lastSong;
    }

    public boolean getShowUnplayableSongs() {
        return showUnplayableSongs;
    }

    public static SettingFile load() {
        try {
            FileInputStream in = new FileInputStream(filename);
            ObjectInputStream s = new ObjectInputStream(in);
            return (SettingFile) s.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException ignored) {
            return new SettingFile();
        }
    }

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(playlists);
        out.writeObject(paths);
        out.writeObject(new URIS(lastSong));
        out.writeBoolean(showUnplayableSongs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.playlists = (ArrayList<PlaylistExternalizable>) in.readObject();
        this.paths = (ArrayList<String>) in.readObject();
        this.lastSong = ((URIS) in.readObject()).getPath();
        this.showUnplayableSongs = in.readBoolean();
    }

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
