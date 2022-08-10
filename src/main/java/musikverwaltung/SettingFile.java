package musikverwaltung;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SettingFile implements Externalizable {

    private static final String filename = "settings.ser";

    // explicitly
    private static final long SerialVersionUID = 10L;
    // TODO testing

    private ObservableList<Playlist> mediaLibrary = FXCollections.observableArrayList();

    private Playlist playlist;

    private Song song;
    private List<String> paths = new ArrayList<>();
    private Path lastSong;


    public static void saveSong(Song song) {
        SettingFile setting = load();
        if (setting.song != song) {
            setting.song = song;
            save(setting);
            System.out.println("added song " + song + "to settingsfile");
        }
    } // TODO what is this?

    public static void savePlaylist(Playlist playlist) {
        SettingFile setting = load();
        if (setting.playlist != playlist) {
            setting.playlist = playlist;
            save(setting);
            System.out.println("added playlist " + playlist + "to settingsfile");
        }
    } // TODO what is this?

    public static void saveMediaLibrary(ObservableList<Playlist> mediaLibrary) {
        SettingFile setting = load();
        if (!Objects.equals(setting.mediaLibrary, mediaLibrary)) {
            setting.mediaLibrary = mediaLibrary;
            save(setting);
            System.out.println("added mediaLibrary " + mediaLibrary + " to settingsfile");
        }
    }

    public static void saveLastSong(Path lastSong) {
        SettingFile setting = load();
        if (setting.lastSong != lastSong) {
            setting.lastSong = lastSong;
            save(setting);
            System.out.println("added lastSong " + lastSong + " to settingsfile");
        }
    }

    public static void savePaths(ArrayList<String> paths) {
        SettingFile setting = load();
        if (setting.paths != paths) {
            setting.paths = paths;
            save(setting);
            System.out.println("added paths " + paths + " to settingsfile");
        }
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
        ArrayList<Playlist> temp = new ArrayList<>(mediaLibrary);
        out.writeObject(temp);
        out.writeObject(playlist);
        out.writeObject(song);
        out.writeObject(paths);
        out.writeUTF(Helper.p2uris(lastSong));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ArrayList<Playlist> temp = (ArrayList<Playlist>) in.readObject();
        ObservableList<Playlist> mediaLibrary = FXCollections.observableArrayList();
        mediaLibrary.addAll(temp);
        setMediaLibrary(mediaLibrary);
        setPlaylist((Playlist) in.readObject());
        setSong((Song) in.readObject());
        setPaths((List<String>) in.readObject());
        setLastSong(Helper.uris2p(in.readUTF()));
    }

    public Path getLastSong() {
        return lastSong;
    }

    public void setLastSong(Path lastSong) {
        this.lastSong = lastSong;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public ObservableList<Playlist> getMediaLibrary() {
        return mediaLibrary;
    }

    public void setMediaLibrary(ObservableList<Playlist> mediaLibrary) {
        this.mediaLibrary = mediaLibrary;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }
}
