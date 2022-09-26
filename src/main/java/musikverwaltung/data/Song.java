package musikverwaltung.data;

import static musikverwaltung.views.MainView.HIGHLIGHT_END;
import static musikverwaltung.views.MainView.HIGHLIGHT_START;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import musikverwaltung.Helper;

/**
 * Representation of the songs in our Musikverwaltung.
 * Gets created with path information of audio file.
 * All other information will be loaded in later, via the mediaManager (which extracts the metadata of the files).
 * Holds information about title, artist, genre, cover image, playability and
 * selection-status/table-position as properties.
 */
public class Song {
    private final Path path;
    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty artist = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();
    private final SimpleObjectProperty<Image> cover = new SimpleObjectProperty<>();
    private ReadOnlyIntegerProperty rowIndex;
    private final SimpleBooleanProperty isPlayable = new SimpleBooleanProperty(true);

    /**
     * @param path is going to be the unique identifier for every Song
     */
    public Song(Path path) {
        this.path = path;
    }

    /**
     * @return title or name of path depending on whether or the song has a title assigned
     */
    public String getPrimaryKey() {
        return title.get() != null ? title.get() : path.getFileName().toString();
    }

    /**
     * @return title of Song or empty String if title is not assigned
     */
    public String getTitle() {
        return notNullString(title.get());
    }

    /**
     * @param titel title of song
     */
    public void setTitle(String titel) {
        this.title.set(titel);
    }

    /**
     * @return property of title
     */
    public SimpleStringProperty getTitleProperty() {
        return title;
    }

    /**
     * @return artist of Song or empty String if artist is not assigned
     */
    public String getArtist() {
        return notNullString(artist.get());
    }

    /**
     * @param artist artist of the song
     */
    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    /**
     * @return property of artist
     */
    public SimpleStringProperty getArtistProperty() {
        return artist;
    }

    /**
     * @return gnere of Song or empty String if genre is not assigned
     */
    public String getGenre() {
        return notNullString(genre.get());
    }


    /**
     * @param genre genre of Song
     */
    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    /**
     * @return property of genre
     */
    public SimpleStringProperty getGenreProperty() {
        return genre;
    }

    /**
     * @return Image object
     */
    public Image getCover() {
        return cover.get();
    }

    /**
     * @param cover cover of Song
     */
    public void setCover(Image cover) {
        this.cover.set(cover);
    }

    /**
     * @return property of cover image
     */
    public SimpleObjectProperty<Image> getCoverProperty() {
        return cover;
    }

    /**
     * @return true if rowIndex assigned (which means song selected), else: false
     */
    public boolean isSelected() {
        return this.rowIndex != null;
    }

    /**
     * undefines rowIndex (deselects song)
     */
    public void deselect() {
        this.rowIndex = null;
    }

    /**
     * defines rowIndex (selects song)
     */
    public void select(ReadOnlyIntegerProperty rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * @return rowIndex (row in tableView where song is selected)
     */
    public int getRowIndex() {
        if (rowIndex == null) {
            return -1;
        }
        return rowIndex.get();
    }

    /**
     * @return path to audio file
     */
    public Path getPath() {
        return path;
    }

    /**
     * @return isPlayable
     */
    public boolean isPlayable() {
        return isPlayable.get();
    }

    /**
     * @param isPlayable  sets the information that Song can be played
     */
    public void setPlayable(boolean isPlayable) {
        this.isPlayable.set(isPlayable);
    }

    /**
     * @return isPlayable
     */
    public SimpleBooleanProperty getPlayableProperty() {
        return isPlayable;
    }

    /**
     * Searches the user given search word throughout the Song object categories (name, artist, genre)
     *
     * @param searchKey search Word from the user
     * @return if searchKey is in any search category (name, artist, genre) of the Song -> true, else: false
     */
    public boolean searchEverywhere(String searchKey) {
        return getPrimaryKey().toLowerCase().contains(searchKey.toLowerCase().trim())
                || getArtist().toLowerCase().contains(searchKey.toLowerCase().trim())
                || getGenre().toLowerCase().contains(searchKey.toLowerCase().trim());
    }

    /**
     * This method adds to matching substrings of title a prefix and suffix.
     * If no match is found the title is returned without modification.
     *
     * @param searchTitle Text like "Atem"
     * @return Highlighted title like {@code <HIGHLIGHT_START>Atem<HIGHLIGHT_END>los}
     */
    public String getHighlightedPrimaryKey(String searchTitle) {
        return getHighlighted(getPrimaryKey(), searchTitle);
    }

    /**
     * This method adds to matching substrings of artist a prefix and suffix.
     * If no match is found the artist is returned without modification.
     *
     * @param searchInterpret Text like "Helene"
     * @return Highlighted artist like {@code <HIGHLIGHT_START>Helene<HIGHLIGHT_END> Fischer}
     */
    public String getHighlightedArtist(String searchInterpret) {
        return getHighlighted(getArtist(), searchInterpret);
    }
    /**
     * This method adds to matching substrings of genre a prefix and suffix.
     * If no match is found the genre is returned without modification.
     *
     * @param searchText Text like "ager"
     * @return Highlighted genre like {@code Schl<HIGHLIGHT_START>ager<HIGHLIGHT_END>}
     */

    public String getHighlightedGenre(String searchText) {
        return getHighlighted(getGenre(), searchText);
    }


    /**
     * This method adds to matching substrings a prefix and suffix.
     * If no match is found the text is returned without modification.
     *
     * @param text Text like "Helene Fischer".
     * @param searchText Text like "Helene".
     * @return Highlighted text like {@code <HIGHLIGHT_START>Helene<HIGHLIGHT_END> Fischer}.
     */
    private static String getHighlighted(String text, String searchText) {
        if (searchText.length() > 3) {
            searchText = searchText.toLowerCase();
            StringBuilder newTitle = new StringBuilder();
            while (text.toLowerCase().contains(searchText)) {
                int matchPreIndex = text.toLowerCase().indexOf(searchText);
                int matchPostIndex = matchPreIndex + searchText.length();
                newTitle.append(text, 0, matchPreIndex).append(HIGHLIGHT_START)
                        .append(text, matchPreIndex, matchPostIndex).append(HIGHLIGHT_END);
                text = text.substring(matchPostIndex);
            }
            text = newTitle + text;
        }
        return text;
    }

    /**
     * @param str any given String object
     * @return str or "" if String is undefined
     */
    private static String notNullString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    /**
     * @return the String representation of the Song object
     */
    @Override
    public String toString() {
        LinkedHashMap<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("title", getTitle());
        attributes.put("artist", getArtist());
        attributes.put("genre", getGenre());
        attributes.put("path", getPath());
        attributes.put("rowIndex", getRowIndex());
        attributes.put("cover", getCover() != null);
        return Helper.toString(this, attributes);
    }

    /**
     * two Song objects are defined identical if their path is the same
     * we only need to compare the paths of any given two songs because it is the songs unique identifier
     *
     * @param other any given object
     * @return true if both songs have same path, else: false
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Song)) {
            return false;
        }
        Song otherSong = (Song) other;
        return Objects.equals(path, otherSong.getPath());
    }

    /**
     * @return hashCode of Song object
     */
    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
