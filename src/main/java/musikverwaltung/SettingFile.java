package musikverwaltung;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.collections.ObservableList;

public class SettingFile implements Serializable {

    private static final String filename = "settings.ser";

    // explicitly
    private static final long SerialVersionUID = 10L;
    // TODO testing

    private ArrayList<Playlist> mediaLibrary = new ArrayList<>();

    private Playlist playlist;

    private Song song;
    private List<String> paths = new ArrayList<>();
    private Path lastSong;


    public static void setSOOONG(Song song) {
        SettingFile setting = load();
        if (setting.song != song) {
            setting.song = song;
            save(setting);
            System.out.println("added song " + song + "to settingsfile");
        }
    } // TODO what is this?

    public static void setPLAAAY(Playlist playlist) {
        SettingFile setting = load();
        if (setting.playlist != playlist) {
            setting.playlist = playlist;
            save(setting);
            System.out.println("added playlist " + playlist + "to settingsfile");
        }
    } // TODO what is this?

    public static void setMediaLibrary(ObservableList<Playlist> mediaLibrary) {
        SettingFile setting = load();
        ArrayList<Playlist> temp = new ArrayList<>(mediaLibrary);
        if (!Objects.equals(setting.mediaLibrary, temp)) {
            setting.mediaLibrary = temp;
            save(setting);
            System.out.println("added mediaLibrary " + mediaLibrary + " to settingsfile");
        }
    }

    public static void setLastSong(Path lastSong) {
        SettingFile setting = load();
        if (setting.lastSong != lastSong) {
            setting.lastSong = lastSong;
            save(setting);
            System.out.println("added lastSong " + lastSong + " to settingsfile");
        }
    }

    public static void setPaths(ArrayList<String> paths) {
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

    public Path getLastSong() {
        return lastSong;
    }

    public Song getSong() {
        return song;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public ArrayList<Playlist> getMediaLibrary() {
        return mediaLibrary;
    }

    public List<String> getPaths() {
        return paths;
    }
}
