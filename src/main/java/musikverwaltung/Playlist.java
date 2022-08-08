package musikverwaltung;

import java.io.*;
import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Playlist implements Externalizable {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final SimpleStringProperty previewImagePath = new SimpleStringProperty();
    private ObservableList<Song> songs = FXCollections.observableArrayList();

    public Playlist() {
        this.name.setValue("Playlist 1");
    }

    public Playlist(String name, ObservableList<Song> playlist) {
        this.name.setValue(name);
        this.songs = playlist;
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

    public void setPreviewImage(String path) {
        //TODO paths austesten
        //https://stackoverflow.com/questions/1697303/is-there-a-java-utility-which-will-convert-a-string-path-to-use-the-correct-file
        /*String correctSeperator = FileSystems.getDefault().getSeparator();
        System.out.println("file sep = " + File.separatorChar);
        if (path==null) return;
        if (!path.contains(File.separator)) {
                // From Windows to Linux/Mac
                path = path.replace('/', File.separatorChar);
                // From Linux/Mac to Windows
                path = path.replace('\\', File.separatorChar);
        }*/
        //path = path.replace("\"", "\\"");
        //path = Paths.get(path).toString();
        //System.out.println(path);
        previewImagePath.set(path);
    }

    public String getPreviewImage() {
        return previewImagePath.get();
    }

    public SimpleStringProperty getPreviewImageProperty() {
        return previewImagePath;
    }

    public ObservableList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ObservableList<Song> playlist) {
        this.songs = playlist;
    }

    public Song getSong(int index) {
        return songs.get(index);
    }

    public void setSong(int index, Song newSong) {
        songs.set(index, newSong);
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

    public Playlist copy() {
        Playlist copyPlaylist = new Playlist();
        copyPlaylist.setName(this.getName());
        for (Song song : this.getSongs()) {
            copyPlaylist.add(song);
        }
        return copyPlaylist;
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
        ArrayList<Song> temp = new ArrayList<>(getSongs());
        out.writeObject(getName());
        out.writeObject(temp);
        out.writeObject(getPreviewImage());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setName((String)in.readObject());
        //System.out.println(getName());
        ArrayList<Song> temp = (ArrayList<Song>)in.readObject();
        ObservableList<Song> songs = FXCollections.observableArrayList();
        songs.addAll(temp);
        setSongs(songs);
        //System.out.println(getSongs());
        setPreviewImage((String)in.readObject());
        //System.out.println(getPreviewImage());
    }
}
