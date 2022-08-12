package musikverwaltung;

import static musikverwaltung.views.MainView.HIGHLIGHT_END;
import static musikverwaltung.views.MainView.HIGHLIGHT_START;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Path;
import java.util.Objects;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Song implements Externalizable {

    // explicitly
    @SuppressWarnings("unused")
    private static final long SerialVersionUID = 30L;

    private final SimpleStringProperty title = new SimpleStringProperty();
    private final SimpleStringProperty artist = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();
    private ReadOnlyIntegerProperty rowIndex;
    private Path path;
    private boolean isPlayable = true;

    @SuppressWarnings("unused")
    public Song(String titel, String artist, String genre, Path path) {
        this.title.setValue(titel);
        this.artist.setValue(artist);
        this.genre.setValue(genre);
        this.path = path;
    }

    public Song(Path path) {
        this.path = path;
    }

    // Externalizable needs a public no-args constructor
    public Song() {
    }

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

    public void setPath(Path path) {
        this.path = path;
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

    private String notNullString(String str) {
        if (str == null) {
            return "";
        }
        return str;
    }

    @Override
    public String toString() {
        // TODO this toString to every important custom class
        return "<" + this.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this)) + "> "
                + "title: " + getTitle() + ", "
                + "artist: " + getArtist() + ", "
                + "genre: " + getGenre() + ", "
                + "path: " + getPath() + ", "
                + "rowIndex: " + getRowIndex();
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
        return this.getTitle().equals(otherSong.getTitle())
                && this.getArtist().equals(otherSong.getArtist())
                && this.getGenre().equals(otherSong.getGenre())
                && this.getPath().equals(otherSong.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, genre, path);
    }

    //https://www.geeksforgeeks.org/externalizable-interface-java/
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(getTitle());
        out.writeUTF(getGenre());
        out.writeUTF(getArtist());
        out.writeUTF(getPath().toAbsolutePath().toString());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        setTitle(in.readUTF());
        setGenre(in.readUTF());
        setArtist(in.readUTF());
        setPath(Helper.s2p(in.readUTF()));
    }
}
