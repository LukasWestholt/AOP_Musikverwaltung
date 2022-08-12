package musikverwaltung;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Playlist implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 20L;

    private final SimpleStringProperty name = new SimpleStringProperty();

    private final ObservableSongQueue songs = new ObservableSongQueue();
    private final SimpleObjectProperty<Image> previewImage = new SimpleObjectProperty<>();

    private Song lastPlayedSong;

    public Playlist() {
        this.name.setValue("Playlist 1");
    }

    public Playlist(String name, ObservableList<Song> playlist) {
        this.name.setValue(name);
        songs.setAll(playlist);
    }

    public Playlist(Playlist baseOfCopy) {
        this.name.setValue(baseOfCopy.getName());
        this.lastPlayedSong = baseOfCopy.getLastPlayedSong();
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

    public void setAll(List<Song> playlist) {
        songs.setAll(playlist);
    }

    public void add(Song song) {
        songs.add(song);
    }

    public void remove(int index) {
        songs.remove(index);
    }

    public boolean removeFirstOccurrence(Song song) {
        return songs.removeFirstOccurrence(song);
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
        setAll((ArrayList<Song>) in.readObject());
        setPreviewImage(in.readUTF());
    }

    public Song nextSong(boolean onRepeat) {
        System.out.println("Queue: " + songs.stream().map(Song::getPrimaryKey).collect(Collectors.toList()));
        Song nextSong = songs.pollFirst();
        if (nextSong == null) {
            // this playlist is empty
            return null;
        }
        songs.addLast(nextSong);
        if (!nextSong.isPlayable()) {
            return null;
        }
        if (nextSong == lastPlayedSong) {
            return nextSong(onRepeat);
        }
        lastPlayedSong = nextSong;
        return nextSong;
    }

    public Song beforeSong(boolean onRepeat) {
        System.out.println("Queue: " + songs.stream().map(Song::getPrimaryKey).collect(Collectors.toList()));
        Song nextSong = songs.pollLast();
        if (nextSong == null) {
            // this playlist is empty
            return null;
        }
        songs.addFirst(nextSong);
        if (!nextSong.isPlayable()) {
            return null;
        }
        if (nextSong == lastPlayedSong) {
            return beforeSong(onRepeat);
        }
        lastPlayedSong = nextSong;
        return nextSong;
    }

    public Song getLastPlayedSong() {
        return lastPlayedSong;
    }
}
