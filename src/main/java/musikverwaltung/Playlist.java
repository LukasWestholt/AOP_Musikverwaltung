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

public class Playlist implements Externalizable {
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final ObservableList<Song> songs = FXCollections.observableArrayList();
    private final SimpleObjectProperty<Path> previewImage = new SimpleObjectProperty<>();

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

    public Path getPreviewImage() {
        return previewImage.get();
    }

    public SimpleObjectProperty<Path> getPreviewImageProperty() {
        return previewImage;
    }

    public void setPreviewImage(Path path) {
        previewImage.set(path);
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
        return "PlayList{" + "name=" + getName() + ", songs=" + songs + " mediafile: " + getPreviewImage() + '}';
    }
    //TODO equals method ausgiebig testen

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Playlist otherPlaylist)) {
            return false;
        }
        if (!(this.getName().equals(otherPlaylist.getName()))) {
            return false;
        }
        if (this.size() != otherPlaylist.size()) {
            return false;
        }
        for (Song otherSong : otherPlaylist.songs) {
            if (!this.songs.contains(otherSong)) {
                return false;
            }
        }
        //ist der auch Name einer Playlist wichtig oder nur der Inhalt?
        return true;
    }

    //https://www.geeksforgeeks.org/externalizable-interface-java/
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        ArrayList<Song> temp = new ArrayList<>(getAll());
        out.writeObject(getName());
        out.writeObject(temp);
        out.writeObject(getPreviewImage());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName((String) in.readObject());
        //System.out.println(getName());
        ArrayList<Song> temp = (ArrayList<Song>) in.readObject();
        ObservableList<Song> songs = FXCollections.observableArrayList();
        songs.addAll(temp);
        setAll(songs);
        //System.out.println(getSongs());
        setPreviewImage((Path) in.readObject());
        //System.out.println(getPreviewImage());
    }
}
