package musikverwaltung.views;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.beans.binding.When;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import musikverwaltung.*;

public class PlaylistView extends MenuBarView {

    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    final FilteredList<Song> flSongs;

    private Playlist contextPlaylist;

    public PlaylistView(ScreenController sc, MediaManager mediaManager) {
        super(sc);
        Runnable saveTask = new SettingsSaveTask(playlists);
        Thread saveTaskThread = new Thread(saveTask);
        saveTaskThread.start();

        flSongs = new FilteredList<>(mediaManager.music);

        addActiveMenuButton(settingViewButton,
                e -> screenController.activateWindow(SettingsView.class, false)
        );
        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(MainView.class)
        );
        addActiveMenuButton(creditsViewButton,
                e -> screenController.activateWindow(CreditsView.class, false)
        );
        setActiveMenuItem(playlistViewButton);

        final Label welcomeLabel = new Label("Playlisten");
        welcomeLabel.getStyleClass().add("header");

        final TilePane tilePane = new TilePane();
        tilePane.setId("playlists");
        tilePane.setHgap(5);
        tilePane.setVgap(5);
        tilePane.setPadding(new Insets(5, 5, 0, 5));
        tilePane.setPrefColumns(1);
        tilePane.setMaxHeight(Region.USE_PREF_SIZE);

        final MenuItem deleteMenu = new MenuItem("Löschen");
        final MenuItem selectMenu = new MenuItem("Bild auswählen");
        final TextField nameField = new TextField();
        final ImageButton resetRenameButton = new ImageButton(
                Helper.getResourcePath(this.getClass(), "/icons/reset.png", false),
                true, false
        );
        final HBox renameBox = new HBox();

        resetRenameButton.setOnAction(e -> nameField.setText(contextPlaylist.getName()));
        resetRenameButton.setPrefSize(30, 30);
        renameBox.setAlignment(Pos.CENTER);
        renameBox.getChildren().addAll(nameField, resetRenameButton);
        CustomMenuItem renameMenu = new CustomMenuItem(renameBox);
        renameMenu.setHideOnClick(false);
        final MenuItem openMenu = new MenuItem("Zeige");
        deleteMenu.setOnAction(action -> playlists.remove(contextPlaylist));
        selectMenu.setOnAction(action -> {
            final Path imageFile = CachedPathChooser.showOpenDialog(stage,
                    "Suche dir ein Vorschaubild für die Playlist aus");
            if (imageFile == null) {
                return;
            }
            try {
                contextPlaylist.setPreviewImage(imageFile);
            } catch (InvalidPathException ignored) {
                System.out.println("Problem on loading File");
            }
        });
        openMenu.setOnAction(action -> {
            final GenericView view = screenController.activateWindow(PlaylistDetailView.class, false);
            if (view instanceof PlaylistDetailView) {
                PlaylistDetailView playlistDetailView = (PlaylistDetailView) view;
                playlistDetailView.setPlaylist(contextPlaylist);
            }
        });
        final ContextMenu quickOptions = new ContextMenu();
        quickOptions.getItems().addAll(deleteMenu, selectMenu, renameMenu, openMenu);
        int[] sepIdices = {2, 4};
        for (int sepIndex : sepIdices) {
            final SeparatorMenuItem sep = new SeparatorMenuItem();
            quickOptions.getItems().add(sepIndex, sep);
        }
        quickOptions.setOnAutoHide(event -> contextPlaylist.setName(nameField.getText()));

        playlists.addListener((ListChangeListener<? super Playlist>) change -> {
            tilePane.getChildren().clear();
            for (Playlist playlist : playlists) {
                final Button playlistButton = new Button(playlist.getName());
                playlistButton.setMinWidth(Region.USE_PREF_SIZE);
                playlistButton.setWrapText(true);
                playlistButton.hoverProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue) {
                        playlistButton.setStyle("-fx-font-size:14");
                    } else {
                        playlistButton.setStyle("-fx-font-size:18");
                    }
                });
                playlistButton.setOnMouseClicked(event -> {
                    if (event.isPopupTrigger()) {
                        contextPlaylist = playlist;
                        nameField.setText(contextPlaylist.getName());
                    }
                });
                playlistButton.setContextMenu(quickOptions);

                playlistButton.setOnAction((e) -> {
                    final GenericView view = screenController.activateWindow(SongView.class, true);
                    if (view instanceof SongView) {
                        SongView songView = (SongView) view;
                        songView.setPlaylist(playlist, true);
                    }
                });
                final ImageView previewView = new ImageView();
                previewView.imageProperty().bind(playlist.getPreviewImageProperty());
                previewView.setFitWidth(80);
                previewView.setFitHeight(80);
                previewView.setPreserveRatio(true);
                playlistButton.graphicProperty().bind(new When(playlist.getPreviewImageProperty().isNull())
                        .then((ImageView) null).otherwise(previewView));
                playlistButton.textProperty().bind(playlist.getNameProperty());
                playlistButton.setAlignment(Pos.BASELINE_CENTER);
                playlistButton.setStyle("-fx-font-size:20");
                playlistButton.setPrefHeight(100);
                playlistButton.setPrefWidth(175);
                tilePane.getChildren().add(playlistButton);
            }
        });
        //letzte Playlisten werden geladen
        playlists.addAll(SettingFile.load().getMediaLibrary());

        final ScrollPane sp = new ScrollPane();
        sp.setId("scroll-playlists");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        sp.setFitToWidth(true);
        sp.setContent(tilePane);
        sp.setMaxHeight(Control.USE_PREF_SIZE);

        final Button musicPlayerButton = new Button("Player");
        musicPlayerButton.setMinWidth(Control.USE_PREF_SIZE);
        musicPlayerButton.setOnAction(e -> {
            final GenericView view = screenController.activateWindow(SongView.class, true);
            if (view instanceof SongView) {
                final Song lastSong = mediaManager.getLastSong();
                if (lastSong != null) {
                    Playlist singleSongPlaylist = new Playlist();
                    singleSongPlaylist.add(lastSong);
                    singleSongPlaylist.setName(lastSong.getTitle());
                    SongView songView = (SongView) view;
                    songView.setPlaylist(singleSongPlaylist, false);
                }
            }
        });

        //TODO verbuggt beim mehrmaligen aktivieren?
        final Button automaticPlaylistButton = new Button("Playlist Vorschläge");
        automaticPlaylistButton.setMinWidth(Control.USE_PREF_SIZE);
        automaticPlaylistButton.setOnAction(e -> createAutomaticPlaylists());

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(welcomeLabel, sp, musicPlayerButton, automaticPlaylistButton);
        showNodes(vbox);

        setDestroyListener(() -> SettingFile.saveMediaLibrary(playlists));
    }

    public boolean addPlaylist(Playlist createdPlaylist) {
        //damit nicht mehrere Playlisten selben inhalts erstellt werden
        if (playlists.contains(createdPlaylist)) {
            return false;
        }
        System.out.println("added a new playlist " + createdPlaylist.getName());
        final Playlist copyPlaylist = new Playlist(createdPlaylist);
        playlists.add(copyPlaylist);

        // TODO Asynchron speichern der aktuellen Playlisten
        return true;
    }

    private void createAutomaticPlaylists() {
        final int threshold = 5;
        boolean isCreatedAlready;
        //Criteria: genre and artist
        final ArrayList<String> genreCriteria = new ArrayList<>();
        final ArrayList<String> artistCriteria = new ArrayList<>();
        for (Song flSong : flSongs) {
            if (!(genreCriteria.contains(flSong.getGenre())) && !(flSong.getGenre().equals(""))) {
                genreCriteria.add(flSong.getGenre());
            } else if (!(artistCriteria.contains(flSong.getArtist()))
                    && !(flSong.getArtist().equals(""))) {
                artistCriteria.add(flSong.getArtist());
            }
        }
        //System.out.println(genreCriteria + "\n" + artistCriteria);

        for (String genre : genreCriteria) {
            flSongs.setPredicate(p -> p.getGenre().contains(genre));
            if (flSongs.size() >= threshold) {
                final Playlist automaticPlaylist = new Playlist();
                automaticPlaylist.setName("Genre: " + genre);
                for (Song song : flSongs) {
                    automaticPlaylist.add(song);
                }
                isCreatedAlready = false;
                for (Playlist playlist:playlists) {
                    if (playlist.isAlmostEqual(automaticPlaylist))
                        isCreatedAlready = true;
                }
                if (!isCreatedAlready) {
                    addPlaylist(automaticPlaylist);
                }
            }
        }
        flSongs.setPredicate(null);
        for (String artist : artistCriteria) {
            flSongs.setPredicate(p -> p.getArtist().contains(artist));
            if (flSongs.size() >= threshold) {
                final Playlist automaticPlaylist = new Playlist();
                automaticPlaylist.setName("Artist: " + artist);
                for (Song song : flSongs) {
                    automaticPlaylist.add(song);
                }
                isCreatedAlready = false;
                for (Playlist playlist:playlists) {
                    if (playlist.isAlmostEqual(automaticPlaylist))
                        isCreatedAlready = true;
                }
                if (!isCreatedAlready) {
                    addPlaylist(automaticPlaylist);
                }
            }
        }
        flSongs.setPredicate(null);
    }
}
