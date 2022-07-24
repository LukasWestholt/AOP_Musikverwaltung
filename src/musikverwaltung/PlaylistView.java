package musikverwaltung;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class PlaylistView extends MenuBarView {

    public PlaylistView(ScreenController sc) {
        super(sc);

        addActiveMenuButton(settingButton,
                e -> screenController.activateWindow("Einstellungen", false, 350, 300)
        );
        addActiveMenuButton(mainViewButton,
                e -> screenController.activate("Musikverwaltung")
        );
        setActiveMenuItem(playlistButton);
    }

    public void prepare() {
        TilePane tilePane = new TilePane();

        final Label welcomeLabel = new Label("Playlisten");
        welcomeLabel.setFont(new Font("Arial", 20));

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        for (int i = 0; i < 10; i++) {
            Button button = new Button(Integer.toString(i));
            button.setPrefHeight(25);
            button.setPrefWidth(25);
            tilePane.getChildren().add(button);
        }
        vbox.getChildren().addAll(welcomeLabel, tilePane);
        showNodes(vbox);
    }
}
