package musikverwaltung.data;

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

    /**
     * gets created with name, and list of songs
     *
     * @param name = name of Playlist
     * @param playlist = list of songs in the playlist
     */
    public Playlist(String name, List<Song> playlist) {
        this.name.setValue(name);
        this.songs.setAll(playlist);
    }

    /**
     * gets created with name, and list of songs and identifier for preview image
     * @param name = name of Playlist
     * @param playlist = list of songs in the playlist
     * @param previewImagePath = identifier for preview image
     */
    public Playlist(String name, List<Song> playlist, URIS previewImagePath) {
        this.name.setValue(name);
        this.songs.setAll(playlist);
        setPreviewImage(previewImagePath);
    }

    /**
     * @return name of playlist
     */
    public String getName() {
        return name.get();
    }

    /**
     * @return property of name
     */
    public SimpleStringProperty getNameProperty() {
        return name;
    }

    /**
     * @param name = name of playlist
     */
    public void setName(String name) {
        this.name.setValue(name);
    }

    /**
     * @return Image object of Playlist
     */
    public Image getPreviewImage() {
        return previewImage.get();
    }

    /**
     * @return url as String from Image object of Playlist
     */
    public String getPreviewImageUrl() {
        Image image = getPreviewImage();
        if (image == null) {
            return "";
        }
        return image.getUrl();
    }

    /**
     * @param uris = identifier for preview image
     */
    public void setPreviewImage(URIS uris) {
        String string = uris.toString();
        if (string != null && !string.isEmpty()) {
            previewImage.set(new Image(string));
        }
    }

    /**
     * @return property for preview image
     */
    public SimpleObjectProperty<Image> getPreviewImageProperty() {
        return previewImage;
    }

    /**
     * @param index = index
     * @return position of Song in playlist
     */
    public Song get(int index) {
        return songs.get(index);
    }

    /**
     * @return observable list containing all songs
     */
    public ObservableList<Song> getAll() {
        return songs;
    }

    /**
     * @param index = index of song that will be removed from playlist
     */
    public void remove(int index) {
        songs.remove(index);
    }

    /**
     * @param song = song in playlist
     * @return information whether first occurence of the song was successfully removed or not
     */
    public boolean removeFirstOccurrence(Song song) {
        return songs.removeFirstOccurrence(song);
    }

    /**
     * @param searchSong = any Song object
     * @return true of song is part of Playlist, else: false
     */
    public boolean contains(Song searchSong) {
        return songs.contains(searchSong);
    }

    /**
     * @return true of playlist contains no songs, else: false
     */
    public boolean isEmpty() {
        return songs.isEmpty();
    }

    /**
     * @return length of Playlist
     */
    public int size() {
        return songs.size();
    }

    //TODO:
    /**
     * @param index
     * @param onRepeat
     * @return
     */
    public Song getRelativeSong(int index, boolean onRepeat) {
        System.out.println("Queue(" + index + "/" + songs.getRemainingSongs() + "): "
                + songs.stream().map(Song::getPrimaryKey).collect(Collectors.toList())
                + " onRepeat: " + onRepeat);

        if (songs.size() == 0) {
            return null;
        }

        // if going backwards and there is still a remaining song
        if (index < 0 && (onRepeat || songs.getRemainingSongs() != songs.size())) {
            Song nextSong = songs.circleBackwards();
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
                reset();
                return null;
            }
            Song nextSong = songs.circleForwards();
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
            return lastPlayedSong;
        }
    }

    //TODO
    /**
     *
     */
    public void reset() {
        lastPlayedSong = null;
        songs.reset();
    }

    public void onSwitchRepeat(boolean newOnSwitch) {
        if (!newOnSwitch) {
            int relativePositionOfFirstSong = songs.getRelativePositionOfFirstSong();
            if (relativePositionOfFirstSong != -1) {
                songs.setRemainingSongs(relativePositionOfFirstSong);
            }
        } else {
            songs.setRemainingSongs(songs.size());
        }
    }

    /**
     *
     *
     * @return the String representation of the Playlist object
     */
    @Override
    public String toString() {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("name", getName());
        attributes.put("songs", songs);
        attributes.put("previewImage", getPreviewImage() != null);
        return Helper.toString(this, attributes);
    }

    /**
     * @param otherPlaylist = any playlist object
     * @return true if both playlists have the same songs, else: false
     */
    public boolean isAlmostEqual(Playlist otherPlaylist) {
        return this.getAll().equals(otherPlaylist.getAll());
    }

    /**
     * two Playlist objects are defined identical if they share the same name, songs and preview image url
     *
     * @param other = any object
     * @return true if both Playlist are identical, else: false
     */
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

    /**
     * @return hashCode of Playlist object
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, songs, previewImage);
    }
}
