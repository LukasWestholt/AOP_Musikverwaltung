package musikverwaltung.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import javafx.scene.image.Image;
import musikverwaltung.Helper;

public class PlaylistExternalizable implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 20L;

    private String name;

    private final ArrayList<String> songs = new ArrayList<>();
    private String previewImagePath;

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

    // Externalizable needs a public no-args constructor
    public PlaylistExternalizable() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreviewImagePath() {
        return previewImagePath;
    }

    public void setPreviewImagePath(String path) {
        this.previewImagePath = path;
    }

    public ArrayList<String> getPaths() {
        return songs;
    }

    public void addPaths(ArrayList<String> songs) {
        this.songs.addAll(songs);
    }

    //https://www.geeksforgeeks.org/externalizable-interface-java/
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeObject(songs);
        out.writeUTF(previewImagePath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName(in.readUTF());
        addPaths((ArrayList<String>) in.readObject());
        setPreviewImagePath(in.readUTF());
    }
}
