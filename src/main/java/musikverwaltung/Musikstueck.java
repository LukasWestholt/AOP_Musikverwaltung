package musikverwaltung;

import static musikverwaltung.views.MainView.HIGHLIGHT_END;
import static musikverwaltung.views.MainView.HIGHLIGHT_START;

import java.io.File;
import javafx.beans.property.SimpleStringProperty;

public class Musikstueck {
    private final SimpleStringProperty titel = new SimpleStringProperty();
    private final SimpleStringProperty interpret = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();
    private final File path;

    @SuppressWarnings("unused")
    public Musikstueck(String titel, String interpret, String genre, File path) {
        this.titel.setValue(titel);
        this.interpret.setValue(interpret);
        this.genre.setValue(genre);
        this.path = path;
    }

    public Musikstueck(File path) {
        this.path = path;
    }

    public String bekommePrimaryKey() {
        return titel.get() != null ? titel.get() : path.getName();
    }

    public String bekommeTitel() {
        return notNullString(titel.get());
    }

    public void setzeTitel(String titel) {
        this.titel.set(titel);
    }

    public SimpleStringProperty bekommeTitelProperty() {
        return titel;
    }

    public String bekommeInterpret() {
        return notNullString(interpret.get());
    }

    public void setzeInterpret(String interpret) {
        this.interpret.set(interpret);
    }

    public SimpleStringProperty bekommeInterpretProperty() {
        return interpret;
    }

    public String bekommeGenre() {
        return notNullString(genre.get());
    }

    public void setzeGenre(String genre) {
        this.genre.set(genre);
    }

    public SimpleStringProperty bekommeGenreProperty() {
        return genre;
    }

    public File getPath() {
        return path;
    }

    public boolean search_everywhere(String searchKey) {
        return bekommePrimaryKey().toLowerCase().contains(searchKey.toLowerCase().trim())
                || bekommeInterpret().toLowerCase().contains(searchKey.toLowerCase().trim())
                || bekommeGenre().toLowerCase().contains(searchKey.toLowerCase().trim());
    }

    /**
     * This method adds to matching substrings of title a prefix and suffix.
     * If no match is found the title is returned without modification.
     *
     * @param searchTitle Text like "Atem"
     * @return Highlighted title like {@code <HIGHLIGHT_START>Atem<HIGHLIGHT_END>los}
     */
    public String bekommeHighlightedPrimaryKey(String searchTitle) {
        return bekommeHighlighted(bekommePrimaryKey(), searchTitle);
    }

    /**
     * This method adds to matching substrings of interpret a prefix and suffix.
     * If no match is found the interpret is returned without modification.
     *
     * @param searchInterpret Text like "Helene"
     * @return Highlighted interpret like {@code <HIGHLIGHT_START>Helene<HIGHLIGHT_END> Fischer}
     */
    public String bekommeHighlightedInterpret(String searchInterpret) {
        return bekommeHighlighted(bekommeInterpret(), searchInterpret);
    }
    /**
     * This method adds to matching substrings of genre a prefix and suffix.
     * If no match is found the genre is returned without modification.
     *
     * @param searchText Text like "ager"
     * @return Highlighted genre like {@code Schl<HIGHLIGHT_START>ager<HIGHLIGHT_END>}
     */

    public String bekommeHighlightedGenre(String searchText) {
        return bekommeHighlighted(bekommeGenre(), searchText);
    }


    /**
     * This method adds to matching substrings a prefix and suffix.
     * If no match is found the text is returned without modification.
     *
     * @param text Text like "Helene Fischer"
     * @param searchText Text like "Helene"
     * @return Highlighted text like {@code <HIGHLIGHT_START>Helene<HIGHLIGHT_END> Fischer}
     */
    private static String bekommeHighlighted(String text, String searchText) {
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
        return "<" + this.getClass().getSimpleName() + "> "
                + "Titel: " + bekommeTitel() + ", "
                + "Interpret: " + bekommeInterpret() + ", "
                + "Genre: " + bekommeGenre() + ", "
                + "Path: " + path.toString();
    }
}
