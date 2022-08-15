package musikverwaltung.nodes;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;

public class OpenSongViewButton extends Button {
    public OpenSongViewButton(EventHandler<ActionEvent> handler) {
        super("Player");
        setMinWidth(Control.USE_PREF_SIZE);
        setOnAction(handler);
    }
}
