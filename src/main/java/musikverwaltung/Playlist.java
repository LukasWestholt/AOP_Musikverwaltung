package musikverwaltung;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Playlist implements Externalizable {

    // explicitly
    private static final long SerialVersionUID = 20L;

    private final SimpleStringProperty name = new SimpleStringProperty();
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final SimpleObjectProperty<Image> previewImage = new SimpleObjectProperty<>();

    public Playlist() {
        this.name.setValue("Playlist 1");
    }

    public Playlist(String name, ObservableList<Song> playlist) {
        this.name.setValue(name);
        songs.setAll(playlist);
    }

    public Playlist(Playlist baseOfCopy) {
        this.name.setValue(baseOfCopy.getName());
        for (Song song : baseOfCopy.getAll()) {
            this.add(song);
        }
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty getNameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Image getPreviewImage() {
        return previewImage.get();
    }

    public String getPreviewImageUrl() {
        Image image = getPreviewImage();
        if (image == null) {
            return "";
        }
        return image.getUrl();
    }

    public void setPreviewImage(String string) {
        if (string != null && !string.isEmpty()) {
            previewImage.set(new Image(string));
        }
    }

    public void setPreviewImage(Path path) {
        String string = Helper.p2uris(path);
        setPreviewImage(string);
    }

    public SimpleObjectProperty<Image> getPreviewImageProperty() {
        return previewImage;
    }

    public Song get(int index) {
        return songs.get(index);
    }

    public ObservableList<Song> getAll() {
        return songs;
    }

    public void set(int index, Song newSong) {
        songs.set(index, newSong);
    }

    public void setAll(ObservableList<Song> playlist) {
        songs.setAll(playlist);
    }

    public void add(Song song) {
        songs.add(song);
    }

    public void remove(Song song) {
        songs.remove(song);
    }

    public boolean contains(Song song) {
        return songs.contains(song);
    }

    public boolean isEmpty() {
        return songs.isEmpty();
    }

    public int size() {
        return songs.size();
    }

    @Override
    public String toString() {
        return "PlayList{" + "name=" + getName() + ", songs=" + songs + ", previewImage: " + getPreviewImage() + '}';
    }
    //TODO equals method ausgiebig testen

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Playlist)) {
            return false;
        }
        Playlist otherPlaylist = (Playlist) other;
        if (this.size() != otherPlaylist.size()) {
            return false;
        }

        return this.getName().equals(otherPlaylist.getName())
                && this.getAll().equals(otherPlaylist.getAll())
                && this.getPreviewImageUrl().equals(otherPlaylist.getPreviewImageUrl());
    }

    //https://www.geeksforgeeks.org/externalizable-interface-java/
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ArrayList<Song> temp = new ArrayList<>(getAll());
        out.writeUTF(getName());
        out.writeObject(temp);
        out.writeUTF(getPreviewImageUrl());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName(in.readUTF());
        ArrayList<Song> temp = (ArrayList<Song>) in.readObject();
        ObservableList<Song> songs = FXCollections.observableArrayList();
        songs.addAll(temp);
        setAll(songs);
        setPreviewImage(in.readUTF());
    }
}
