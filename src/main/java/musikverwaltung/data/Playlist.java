package musikverwaltung.data;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import musikverwaltung.Helper;

public class Playlist {
    private final SimpleStringProperty name = new SimpleStringProperty();

    private final ObservableSongQueue songs = new ObservableSongQueue();
    private final SimpleObjectProperty<Image> previewImage = new SimpleObjectProperty<>();

    private Song lastPlayedSong;

    public Playlist() {
        this.name.setValue("Playlist 1");
    }

    public Playlist(String name, ObservableList<Song> playlist) {
        this.name.setValue(name);
        this.songs.setAll(playlist);
    }

    public Playlist(String name, List<Song> playlist, String previewImagePath) {
        this.name.setValue(name);
        this.songs.setAll(playlist);
        setPreviewImage(previewImagePath);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty getNameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
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

    public boolean contains(Song searchSong) {
        return songs.contains(searchSong);
    }

    public boolean isEmpty() {
        return songs.isEmpty();
    }

    public int size() {
        return songs.size();
    }

    public Song getRelativeSong(int index, boolean onRepeat) {
        System.out.println("Queue(" + index + "/" + songs.getRemainingSongs() + "): "
                + songs.stream().map(Song::getPrimaryKey).collect(Collectors.toList()));
        if (songs.size() == 0) {
            return null;
        }

        // if going backwards and there is still a remaining song
        if (index < 0 && (onRepeat || songs.getRemainingSongs() != songs.size())) {
            Song nextSong = songs.removeLast();
            songs.addFirst(nextSong);
            if (!onRepeat) {
                songs.addToRemainingSongs(1);
            }
            if (nextSong == lastPlayedSong && songs.size() > 1) {
                // this is needed for the first skipBackwards-click after a skipForwards-click because at this moment
                // the data structure has the lastPlayedSong at the tail.
                nextSong = getRelativeSong(-1, onRepeat);
            }
            lastPlayedSong = nextSong;
            return getRelativeSong(index + 1, onRepeat);

            // if going forwards
        } else if (index > 0) {
            // return null if there is no remaining song and the end is reached
            if (songs.getRemainingSongs() == 0) {
                // TODO show first song of playlist like in spotify
                return null;
            }
            Song nextSong = songs.removeFirst();
            songs.addLast(nextSong);
            if (!onRepeat) {
                songs.addToRemainingSongs(-1);
            }
            if (nextSong == lastPlayedSong && songs.size() > 1) {
                // this is needed for the first skipForwards-click after a skipBackwards-click because at this moment
                // the data structure has the lastPlayedSong at the head.
                nextSong = getRelativeSong(1, onRepeat);
            }
            lastPlayedSong = nextSong;
            return getRelativeSong(index - 1, onRepeat);

            // else: index==0 or going backwards without remaining song
        } else {
            if (!lastPlayedSong.isPlayable()) {
                return null;
            }
            return lastPlayedSong;
        }
    }

    public void resetRemainingSongs() {
        songs.resetRemainingSongs();
    }

    @Override
    public String toString() {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", getName());
        attributes.put("songs", songs);
        attributes.put("previewImage", getPreviewImage());
        return Helper.toString(this, attributes);
    }

    public boolean isAlmostEqual(Playlist otherPlaylist) {
        return this.getAll().equals(otherPlaylist.getAll());
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(name, songs, previewImage);
    }
}
