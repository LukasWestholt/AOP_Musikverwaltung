package musikverwaltung.views;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;

public class QuickOptionsView extends GenericView {
    private Playlist affectedPlaylist;

    private ObservableList<Playlist> contextLibrary;

    private final TextField inputTextField = new TextField();

    public QuickOptionsView(ScreenController sc) {
        super(sc, 200, 200);

        Button deleteButton = new Button("Löschen");
        deleteButton.setOnAction(e -> deleteAffectedPlaylist());
        Button renameButton = new Button("Umbenennen");
        renameButton.setOnAction(e -> renameAffectedPlaylist());
        Button imageButton = new Button("Bild einfügen");
        imageButton.setOnAction(e -> selectImage());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(deleteButton, renameButton, imageButton, inputTextField);
        vbox.setAlignment(Pos.CENTER);
        showNodes(vbox);
    }

    public void setAffectedPlaylistInContext(ObservableList<Playlist> mediaLibrary, Playlist playlist) {
        contextLibrary = mediaLibrary;
        affectedPlaylist = playlist;
        System.out.println("affected playlist = " + affectedPlaylist);
    }
    public void setAffectedPlaylist(Playlist playlist) {
        System.out.println("affected playlist = " + affectedPlaylist);
        affectedPlaylist = playlist;
    }

    private void renameAffectedPlaylist() {
        String newName = inputTextField.getText();
        if (!newName.equals("")) {
            affectedPlaylist.setName(newName);
            inputTextField.clear();
        }
    }

    private void selectImage() {
        affectedPlaylist.setPreviewImage(inputTextField.getText());
        inputTextField.clear();
    }

    private void deleteAffectedPlaylist() {
        if (contextLibrary!=null) {
            contextLibrary.remove(affectedPlaylist);
        }
    }

}
