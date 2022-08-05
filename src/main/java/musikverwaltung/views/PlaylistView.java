package musikverwaltung.views;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import musikverwaltung.MediaManager;
import musikverwaltung.Musikstueck;
import musikverwaltung.ScreenController;

public class PlaylistView extends MenuBarView {

    public PlaylistView(ScreenController sc, MediaManager mediaManager) {
        super(sc);

        addActiveMenuButton(settingButton,
                e -> screenController.activateWindow(SettingsView.class, false)
        );
        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(MainView.class)
        );
        setActiveMenuItem(playlistButton);

        final Label welcomeLabel = new Label("Playlisten");
        welcomeLabel.getStyleClass().add("header");

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10));

        TilePane tilePane = new TilePane();
        tilePane.setId("playlists");
        tilePane.setHgap(5);
        tilePane.setVgap(5);
        tilePane.setPadding(new Insets(5, 5, 0, 5));
        tilePane.setPrefColumns(1);
        tilePane.setMaxHeight(Region.USE_PREF_SIZE);

        for (int i = 0; i < 12; i++) {
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
                ObservableList<Musikstueck> playlist = mediaManager.music;
                GenericView view = screenController.activateWindow(SongView.class, true);
                if (view instanceof SongView songView) {
                    songView.setPlaylist(playlist, true);
                }
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
        sp.setMaxHeight(Control.USE_PREF_SIZE);

        Button musicPlayerButton = new Button("Player");
        musicPlayerButton.setMinWidth(Control.USE_PREF_SIZE);
        musicPlayerButton.setOnAction(e -> {
            GenericView view = screenController.activateWindow(SongView.class, true);
            if (view instanceof SongView songView) {
                Musikstueck lastSong = mediaManager.getLastSong();
                if (lastSong != null) {
                    ArrayList<Musikstueck> playlist = new ArrayList<>();
                    playlist.add(lastSong);
                    songView.setPlaylist(playlist, false);
                }
            }
        });

        vbox.getChildren().addAll(welcomeLabel, sp, musicPlayerButton);
        showNodes(vbox);
    }
}
