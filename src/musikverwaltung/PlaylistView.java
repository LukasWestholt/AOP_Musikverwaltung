package musikverwaltung;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class PlaylistView extends MenuBarView {

    public PlaylistView(ScreenController sc) {
        super(sc);

        addActiveMenuButton(settingButton,
                e -> screenController.activateWindow(SC.Einstellungen, false, 350, 300)
        );
        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(SC.Musikverwaltung)
        );
        setActiveMenuItem(playlistButton);

        final Label welcomeLabel = new Label("Playlisten");
        welcomeLabel.setFont(new Font("Arial", 20));

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        TilePane tilePane = new TilePane();
        tilePane.setId("playlists");
        tilePane.setHgap(5);
        tilePane.setVgap(5);
        tilePane.setPadding(new Insets(5));

        for (int i = 0; i < 20; i++) {
            Button button = new Button(Integer.toString(i));
            if (i == 0) {
                button.setText("Lalala das ist ein richtig langer langer langer langer langer langer Name");
            }
            button.setMinWidth(Region.USE_PREF_SIZE);
            button.setWrapText(true);
            button.hoverProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue) {
                    button.setStyle("-fx-font-size:15");
                } else {
                    button.setStyle("-fx-font-size:20");
                }
            });
            button.setOnAction((e) -> {
            });
            button.setAlignment(Pos.BASELINE_CENTER);
            button.setStyle("-fx-font-size:20");
            button.setPrefHeight(100);
            button.setPrefWidth(175);
            tilePane.getChildren().add(button);
        }

        ScrollPane sp = new ScrollPane();
        sp.setId("scroll-playlists");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(true);
        sp.setContent(tilePane);

        vbox.getChildren().addAll(welcomeLabel, sp);
        showNodes(vbox);
    }
}
