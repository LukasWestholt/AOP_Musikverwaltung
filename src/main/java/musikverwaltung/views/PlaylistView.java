package musikverwaltung.views;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import musikverwaltung.MediaManager;
import musikverwaltung.Song;
import musikverwaltung.PlayList;
import musikverwaltung.ScreenController;

public class PlaylistView extends MenuBarView {

    private final ObservableList<PlayList> mediaLibrary = FXCollections.observableArrayList();
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

        ListChangeListener mediaLibraryChangeListener = new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                tilePane.getChildren().clear();
                System.out.println("checking ...");
                System.out.println(mediaLibrary);
                for (PlayList playlist: mediaLibrary) {
                    System.out.println(playlist.getName());
                    System.out.println();
                    Button playlistButton = new Button(playlist.getName());
                    playlistButton.setMinWidth(Region.USE_PREF_SIZE);
                    playlistButton.setWrapText(true);
                    playlistButton.hoverProperty().addListener((obs, oldValue, newValue) -> {
                        if (newValue) {
                            playlistButton.setStyle("-fx-font-size:15");
                        } else {
                            playlistButton.setStyle("-fx-font-size:20");
                        }
                    });
                    playlistButton.setOnAction((e) -> {
                        GenericView view = screenController.activateWindow(SongView.class, true);
                        if (view instanceof SongView songView) {
                            songView.setPlaylist(playlist);
                        }
                    });
                    playlistButton.setAlignment(Pos.BASELINE_CENTER);
                    playlistButton.setStyle("-fx-font-size:20");
                    playlistButton.setPrefHeight(100);
                    playlistButton.setPrefWidth(175);
                    tilePane.getChildren().add(playlistButton);
                }
            }
        };

        mediaLibrary.addListener(mediaLibraryChangeListener);

/*
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
                    songView.setPlaylist(playlist);
                }
            });
            button.setAlignment(Pos.BASELINE_CENTER);
            button.setStyle("-fx-font-size:20");
            button.setPrefHeight(100);
            button.setPrefWidth(175);
            tilePane.getChildren().add(button);
        }
*/


        ScrollPane sp = new ScrollPane();
        sp.setId("scroll-playlists");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(true);
        sp.setContent(tilePane);
        sp.setMaxHeight(Control.USE_PREF_SIZE);

        Button musicPlayerButton = new Button("Player");
        musicPlayerButton.setMinWidth(Control.USE_PREF_SIZE);
        musicPlayerButton.setOnAction(e -> screenController.activateWindow(SongView.class, true));

        //TODO delete button ohne auswahl der zu löschenden Playlist ergibt keinen Sinn
        //Button deletePlaylistButton = new Button("Löschen");
        //deletePlaylistButton.setMinWidth(Control.USE_PREF_SIZE);

        Button automaticPlaylistButton = new Button("Playlist Vorschläge");
        automaticPlaylistButton.setMinWidth(Control.USE_PREF_SIZE);

        vbox.getChildren().addAll(welcomeLabel, sp, musicPlayerButton);
        showNodes(vbox);
    }

    public void addPlaylist(PlayList createdPlaylist) {
        //for cloning playlist in mainView can change without interacting with the saved playlists in the media library
        System.out.println("added a new playlist " + createdPlaylist.getName());
        PlayList newPlaylist = new PlayList();
        newPlaylist.setName(createdPlaylist.getName());
        for (Song song:createdPlaylist.getSongs()) {
            newPlaylist.add(song);
        }
        mediaLibrary.add(newPlaylist);
    }


}
