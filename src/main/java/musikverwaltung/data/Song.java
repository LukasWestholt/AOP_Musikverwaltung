package musikverwaltung.data;

import static musikverwaltung.views.MainView.HIGHLIGHT_END;
import static musikverwaltung.views.MainView.HIGHLIGHT_START;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Objects;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import musikverwaltung.Helper;

public class Song {
    // path is identifier
    private Path path;

    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty artist = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();
    private final SimpleObjectProperty<Image> cover = new SimpleObjectProperty<>();
    private ReadOnlyIntegerProperty rowIndex;
    private boolean isPlayable = true;

    public Song(Path path) {
        this.path = path;
    }

    // Externalizable needs a public no-args constructor
    public Song() {}

    public String getPrimaryKey() {
        return title.get() != null ? title.get() : path.getFileName().toString();
    }

    public String getTitle() {
        return notNullString(title.get());
    }

    public void setTitle(String titel) {
        this.title.set(titel);
    }

    public SimpleStringProperty getTitleProperty() {
        return title;
    }

    public String getArtist() {
        return notNullString(artist.get());
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public SimpleStringProperty getArtistProperty() {
        return artist;
    }

    public String getGenre() {
        return notNullString(genre.get());
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public SimpleStringProperty getGenreProperty() {
        return genre;
    }

    public Image getCover() {
        return cover.get();
    }

    public void setCover(Image cover) {
        this.cover.set(cover);
    }

    @SuppressWarnings("unused")
    public SimpleObjectProperty<Image> getCoverProperty() {
        return cover;
    }

    public boolean isSelected() {
        return this.rowIndex != null;
    }

    public void deselect() {
        this.rowIndex = null;
    }

    public void select(ReadOnlyIntegerProperty rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getRowIndex() {
        if (rowIndex == null) {
            return -1;
        }
        return rowIndex.get();
    }

    public Path getPath() {
        return path;
    }

    public boolean isPlayable() {
        return isPlayable;
    }

    public void setPlayable(boolean isPlayable) {
        this.isPlayable = isPlayable;
    }

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

    private static String notNullString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
