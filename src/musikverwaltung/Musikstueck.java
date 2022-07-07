package musikverwaltung;

import javafx.beans.property.SimpleStringProperty;

import static musikverwaltung.MainView.HIGHLIGHT_START;
import static musikverwaltung.MainView.HIGHLIGHT_END;

public class Musikstueck {
    private final SimpleStringProperty titel = new SimpleStringProperty();
    private final SimpleStringProperty interpret = new SimpleStringProperty();
    private final SimpleStringProperty genre = new SimpleStringProperty();

    public Musikstueck(String titel, String interpret, String genre) {
        this.titel.setValue(titel);
        this.interpret.setValue(interpret);
        this.genre.setValue(genre);
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

    public SimpleStringProperty bekommeGenreProperty()
    {
        return genre;
    }

    public boolean search_everywhere(String searchKey) {
        return titel.get().toLowerCase().contains(searchKey.toLowerCase().trim())
                || interpret.get().toLowerCase().contains(searchKey.toLowerCase().trim())
                || genre.get().toLowerCase().contains(searchKey.toLowerCase().trim());
    }

    public String bekommeHighlighted(String text, String searchText) {
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
