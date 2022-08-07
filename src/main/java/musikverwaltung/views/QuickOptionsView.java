package musikverwaltung.views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;

public class QuickOptionsView extends GenericView {
    private Playlist affectedPlaylist;

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
        //TODO auf playlistview zugreifen ohne nochmaliges öffnen und playlist löschen oder decompose methode
        // in playlist einbauen
        GenericView view = screenController.activateWindow(PlaylistView.class, true);
        System.out.println(screenController.getMain());
        System.out.println(screenController.getMainScene());
        System.out.println();
        System.out.println(screenController.getMainScene().getClass());
        System.out.println(view.getClass());
        if (view instanceof PlaylistView playlistView) {
            playlistView.deletePlaylist(affectedPlaylist);
        }
    }

}
