package musikverwaltung;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private final SimpleStringProperty name = new SimpleStringProperty();
    //TODO überlegen ob Playtime wichtig ist oder weglassen
    private final SimpleIntegerProperty playtime = new SimpleIntegerProperty();
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

    public int getPlaytime() {
        return playtime.get();
    }

    public SimpleIntegerProperty getPlaytimeProperty() {
        return playtime;
    }

    public void setPlaytime(int playtime) {
        this.playtime.set(playtime);
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
        return "PlayList{" + "name=" + getName() + ", songs=" + songs + '}';
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
        if (this.size() != otherPlaylist.size()) {
            return false;
        }
        for (Song otherSong : otherPlaylist.songs) {
            if (!this.songs.contains(otherSong)) {
                return false;
            }
        }
        //ist der auch Name einer Playlist wichtig oder nur der Inhalt?
        return this.getName().equals(otherPlaylist.getName());
    }


}
