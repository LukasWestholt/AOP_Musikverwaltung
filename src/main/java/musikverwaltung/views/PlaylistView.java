package musikverwaltung.views;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;
import musikverwaltung.Song;


public class PlaylistView extends MenuBarView {

    private final ObservableList<Playlist> mediaLibrary = FXCollections.observableArrayList();

    final FilteredList<Song> allSongs;

    public PlaylistView(ScreenController sc, MediaManager mediaManager) {
        super(sc);

        allSongs = new FilteredList<>(mediaManager.music, p -> true);

        addActiveMenuButton(settingButton,
                e -> screenController.activateWindow(SettingsView.class, false)
        );
        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(MainView.class)
        );
        addActiveMenuButton(creditsButton,
                e -> screenController.activateWindow(CreditsView.class, false)
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
                System.out.println(mediaLibrary);
                for (Playlist playlist : mediaLibrary) {
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
                            songView.setPlaylist(playlist, true);
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
                Song lastSong = mediaManager.getLastSong();
                if (lastSong != null) {
                    Playlist singleSongPlaylist = new Playlist();
                    singleSongPlaylist.add(lastSong);
                    singleSongPlaylist.setName(lastSong.getTitle());
                    songView.setPlaylist(singleSongPlaylist, false);
                }
            }
        });

        //TODO delete button ohne auswahl der zu löschenden Playlist ergibt keinen Sinn
        //Button deletePlaylistButton = new Button("Löschen");
        //deletePlaylistButton.setMinWidth(Control.USE_PREF_SIZE);

        Button automaticPlaylistButton = new Button("Playlist Vorschläge");
        automaticPlaylistButton.setMinWidth(Control.USE_PREF_SIZE);
        automaticPlaylistButton.setOnAction(e -> createAutomaticPlaylists());

        vbox.getChildren().addAll(welcomeLabel, sp, musicPlayerButton, automaticPlaylistButton);
        showNodes(vbox);
    }

    public boolean addPlaylist(Playlist createdPlaylist) {
        //damit nicht mehrere Playlisten selben inhalts erstellt werden
        if (mediaLibrary.contains(createdPlaylist)) {
            return false;
        }
        //for cloning playlist in mainView can change without interacting with the saved playlists in the media library
        System.out.println("added a new playlist " + createdPlaylist.getName());
        Playlist newPlaylist = createdPlaylist.copy();
        mediaLibrary.add(newPlaylist);
        return true;
    }

    private void createAutomaticPlaylists() {
        int threshold = 10;
        //wenn wir uns für filter entschieden haben kann diese niemals erreichte definition gelöscht werden
        String filterCriteria = " ";
        String[] filter = new String[] {"genre", "artist"};
        for (String filterCategory : filter) {
            for (int i = 0; i < allSongs.size(); i++) {
                if (filterCategory.equals("genre")) {
                    //aufgrund der lamda expression von predicate muss variabel jedes mal neu definiert werden
                    String genreFilterCriteria = allSongs.get(i).getGenre();
                    filterCriteria = genreFilterCriteria;
                    if (!genreFilterCriteria.equals("")) {
                        allSongs.setPredicate(p -> p.getGenre().contains(genreFilterCriteria));
                    }
                }
                if (filterCategory.equals("artist")) {
                    String artistFilterCriteria = allSongs.get(i).getArtist();
                    filterCriteria = artistFilterCriteria;
                    if (!artistFilterCriteria.equals("")) {
                        allSongs.setPredicate(p -> p.getArtist().contains(artistFilterCriteria));
                    }
                }
                //System.out.println(filterCriteria +"-" + "length: " + allSongs.size() + " " + allSongs);
                //manche Songs haben keine eintragungen und liefern nach filtern alle Songs -> werden ignoriert
                if (allSongs.size() >= threshold && !filterCriteria.equals("")) {
                    Playlist automaticPlaylist = new Playlist();
                    automaticPlaylist.setName(filterCategory + ": " + filterCriteria);
                    for (Song song : allSongs) {
                        automaticPlaylist.add(song);
                    }
                    addPlaylist(automaticPlaylist);
                }
                allSongs.setPredicate(null);
            }
        }
    }
}
