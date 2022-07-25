package musikverwaltung;

import javafx.beans.property.SimpleStringProperty;

import static musikverwaltung.MainView.HIGHLIGHT_START;
import static musikverwaltung.MainView.HIGHLIGHT_END;

public class Musikstueck {
    private final SimpleStringProperty titel = new SimpleStringProperty();
    private final SimpleStringProperty interpret = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();
    private final SimpleStringProperty path = new SimpleStringProperty();

    public Musikstueck(String titel, String interpret, String genre, String path) {
        this.titel.setValue(titel);
        this.interpret.setValue(interpret);
        this.genre.setValue(genre);
        this.path.setValue(path);
    }

    public String bekommeTitel() {
        return titel.get();
    }

    public void setzeTitel(String titel) {
        this.titel.set(titel);
    }

    public SimpleStringProperty bekommeTitelProperty()
    {
        return titel;
    }

    public String bekommeInterpret() {
        return interpret.get();
    }

    public void setzeInterpret(String interpret) {
        this.interpret.set(interpret);
    }

    public SimpleStringProperty bekommeInterpretProperty()
    {
        return interpret;
    }

    public String bekommeGenre() {
        return genre.get();
    }

    public void setzeGenre(String genre) {
        this.genre.set(genre);
    }
    
     public String getpath() {
        return path.get();
    }
    
    public void setpath(String path) {
        this.path.set(path);
    }
    
   

    public SimpleStringProperty bekommeGenreProperty()
    {
        return genre;
    }

    public boolean search_everywhere(String searchKey) {
        return titel.get().toLowerCase().contains(searchKey.toLowerCase().trim())
                || interpret.get().toLowerCase().contains(searchKey.toLowerCase().trim())
                || genre.get().toLowerCase().contains(searchKey.toLowerCase().trim());
    }

    /**
     * This method adds to matching substrings of title a prefix and suffix.
     * If no match is found the title is returned without modification.
     * @param searchTitle Text like "Atem"
     * @return Highlighted title like "<HIGHLIGHT_START>Atem<HIGHLIGHT_END>los"
     */
    public String bekommeHighlightedTitel(String searchTitle) {
        return bekommeHighlighted(bekommeTitel(), searchTitle);
    }
    /**
     * This method adds to matching substrings of interpret a prefix and suffix.
     * If no match is found the interpret is returned without modification.
     * @param searchInterpret Text like "Helene"
     * @return Highlighted interpret like "<HIGHLIGHT_START>Helene<HIGHLIGHT_END> Fischer"
     */
    public String bekommeHighlightedInterpret(String searchInterpret) {
        return bekommeHighlighted(bekommeInterpret(), searchInterpret);
    }
    /**
     * This method adds to matching substrings of genre a prefix and suffix.
     * If no match is found the genre is returned without modification.
     * @param searchText Text like "ager"
     * @return Highlighted genre like "Schl<HIGHLIGHT_START>ager<HIGHLIGHT_END>"
     */
    public String bekommeHighlightedGenre(String searchText) {
        return bekommeHighlighted(bekommeGenre(), searchText);
    }


    /**
     * This method adds to matching substrings a prefix and suffix.
     * If no match is found the text is returned without modification.
     * @param text Text like "Helene Fischer"
     * @param searchText Text like "Helene"
     * @return Highlighted text like "<HIGHLIGHT_START>Helene<HIGHLIGHT_END> Fischer"
     */
    private static String bekommeHighlighted(String text, String searchText) {
        if (searchText.length() > 3) {
            searchText = searchText.toLowerCase();
            StringBuilder new_titel = new StringBuilder();
            while (text.toLowerCase().contains(searchText)) {
                int match_pre_index = text.toLowerCase().indexOf(searchText);
                int match_post_index = match_pre_index + searchText.length();
                new_titel.append(text, 0, match_pre_index).append(HIGHLIGHT_START).append(text, match_pre_index, match_post_index).append(HIGHLIGHT_END);
                text = text.substring(match_post_index);
            }
            text = new_titel + text;
        }
        return text;
    }
}