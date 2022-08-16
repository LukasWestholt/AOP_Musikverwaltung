package musikverwaltung.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

public class PlaylistExternalizable implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 20L;

    private String name;

    private final ArrayList<URIS> songs = new ArrayList<>();
    private URIS previewImagePath;

    public PlaylistExternalizable(Playlist playlist) {
        name = playlist.getName();
        for (Song song : playlist.getAll()) {
            songs.add(new URIS(song.getPath()));
        }
        previewImagePath = new URIS(playlist.getPreviewImage());
    }

    // Externalizable needs a public no-args constructor
    public PlaylistExternalizable() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URIS getPreviewImagePath() {
        return previewImagePath;
    }

    public void setPreviewImagePath(URIS uris) {
        this.previewImagePath = uris;
    }

    public ArrayList<URIS> getPaths() {
        return songs;
    }

    public void addPaths(ArrayList<URIS> songs) {
        this.songs.addAll(songs);
    }

    //https://www.geeksforgeeks.org/externalizable-interface-java/
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeObject(songs);
        out.writeObject(previewImagePath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName(in.readUTF());
        addPaths((ArrayList<URIS>) in.readObject());
        setPreviewImagePath((URIS) in.readObject());
    }
}
