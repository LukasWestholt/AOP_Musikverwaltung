package musikverwaltung;

import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingFile implements Serializable {

    private static final String filename = "settings.ser";

    private ArrayList<Playlist> mediaLibrary;

    private List<String> paths = new ArrayList<>();
    private File lastSong;

    public static void setMediaLibrary(ObservableList<Playlist> mediaLibrary) {
        SettingFile setting = load();
        ArrayList<Playlist> temp = new ArrayList<>(mediaLibrary);
        if (!Objects.equals(setting.mediaLibrary, temp)) {
            setting.mediaLibrary = temp;
            save(setting);
        }
        System.out.println("added " + mediaLibrary + " to settingsfile");
    }

    public static void setLastSong(File lastSong) {
        SettingFile setting = load();
        if (setting.lastSong != lastSong) {
            setting.lastSong = lastSong;
            save(setting);
        }
    }

    public static void setPath(ArrayList<String> list) {
        SettingFile setting = load();
        if (setting.paths != list) {
            setting.paths = list;
            save(setting);
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

    public File getLastSong() {
        return lastSong;
    }

    public ArrayList<Playlist> getMediaLibrary() {return mediaLibrary;}

    public List<String> getPaths() {
        return paths;
    }
}
