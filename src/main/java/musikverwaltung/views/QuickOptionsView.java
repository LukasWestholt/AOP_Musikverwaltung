package musikverwaltung.views;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import musikverwaltung.ScreenController;
import musikverwaltung.handler.ActionListenerManager;

public class QuickOptionsView extends GenericView implements ActionListenerManager {

    public QuickOptionsView(ScreenController sc) {
        super(sc);

        Button deleteButton = new Button("Löschen");
        //deleteButton.setOnAction(e -> deletePlaylist);
        Button renameButton = new Button("Umbenennen");
        //renameButton.setOnAction(e -> renamePlaylist);
        Button imageButton = new Button("Bild einfügen");
        //imageButton.setOnAction(e -> selectImage);
        HBox hBox = new HBox(deleteButton, renameButton, imageButton);
        showNodes(hBox);
    }
}
