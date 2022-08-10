package musikverwaltung;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SettingFile implements Externalizable {

    // explicitly
    private static final long SerialVersionUID = 10L;

    private static final String filename = "settings.ser";

    private ObservableList<Playlist> mediaLibrary = FXCollections.observableArrayList();

    private ArrayList<String> paths = new ArrayList<>();
    private Path lastSong;

    private boolean showUnplayableSongs;

    public static void saveMediaLibrary(ObservableList<Playlist> mediaLibrary) {
        SettingFile setting = load();
        if (!setting.mediaLibrary.equals(mediaLibrary)) {
            setting.mediaLibrary = mediaLibrary;
            save(setting);
            System.out.println("added mediaLibrary " + mediaLibrary + " to SettingFile");
        }
    }

    public static void saveLastSong(Path lastSong) {
        SettingFile setting = load();
        if (!Objects.equals(setting.lastSong, lastSong)) {
            setting.lastSong = lastSong;
            save(setting);
            System.out.println("added lastSong " + lastSong + " to SettingFile");
        }
    }

    public static void savePaths(ArrayList<String> paths) {
        SettingFile setting = load();
        if (!Objects.equals(setting.paths, paths)) {
            setting.paths = paths;
            save(setting);
            System.out.println("added paths " + paths + " to SettingFile");
        }
    }

    public static void saveShowUnplayableSongs(boolean showUnplayableSongs) {
        SettingFile setting = load();
        if (setting.showUnplayableSongs != showUnplayableSongs) {
            setting.showUnplayableSongs = showUnplayableSongs;
            save(setting);
            System.out.println("added showUnplayableSongs " + showUnplayableSongs + " to SettingFile");
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
        out.writeObject(paths);
        out.writeUTF(Helper.p2uris(lastSong));
        out.writeBoolean(showUnplayableSongs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ArrayList<Playlist> temp = (ArrayList<Playlist>) in.readObject();
        ObservableList<Playlist> mediaLibrary = FXCollections.observableArrayList();
        mediaLibrary.addAll(temp);
        setMediaLibrary(mediaLibrary);
        setPaths((ArrayList<String>) in.readObject());
        setLastSong(Helper.uris2p(in.readUTF()));
        setShowUnplayableSongs(in.readBoolean());
    }

    public Path getLastSong() {
        return lastSong;
    }

    public void setLastSong(Path lastSong) {
        this.lastSong = lastSong;
    }

    public ObservableList<Playlist> getMediaLibrary() {
        return mediaLibrary;
    }

    public void setMediaLibrary(ObservableList<Playlist> mediaLibrary) {
        this.mediaLibrary = mediaLibrary;
    }

    public ArrayList<String> getPaths() {
        return paths;
    }

    public void setPaths(ArrayList<String> paths) {
        this.paths = paths;
    }

    public boolean getShowUnplayableSongs() {
        return showUnplayableSongs;
    }

    public void setShowUnplayableSongs(boolean showUnplayableSongs) {
        this.showUnplayableSongs = showUnplayableSongs;
    }


}
