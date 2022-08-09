package musikverwaltung.views;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import musikverwaltung.*;


public class PlaylistView extends MenuBarView {

    private final ObservableList<Playlist> mediaLibrary = FXCollections.observableArrayList();

    final FilteredList<Song> allSongs;

    private Playlist contextPlaylist;

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

        //TODO überlegen das öfter einzubauen mit radio und checkMenu sehr praktisch
        final MenuItem deleteMenu = new MenuItem("Löschen");
        final MenuItem selectMenu = new MenuItem("Bild auswählen");
        final TextField nameField = new TextField("");
        final ImageButton resetRenameButton = new ImageButton(
                Helper.getResourceFile(this.getClass(), "/icons/reset.png", false),
                true, false
        );
        final HBox renameBox = new HBox();

        resetRenameButton.setOnAction(e -> nameField.setText(contextPlaylist.getName()));
        resetRenameButton.setPrefSize(30, 30);
        renameBox.setAlignment(Pos.CENTER);
        renameBox.getChildren().addAll(nameField, resetRenameButton);
        CustomMenuItem renameMenu = new CustomMenuItem(renameBox);
        renameMenu.setHideOnClick(false);
        deleteMenu.setOnAction(action -> mediaLibrary.remove(contextPlaylist));
        selectMenu.setOnAction(action -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Suche dir ein Vorschaubild für die Playlist aus");
            File imageFile = fileChooser.showOpenDialog(stage);
            if (imageFile == null) {
                return;
            }
            try {
                Path imagePath = imageFile.toPath();
                contextPlaylist.setPreviewImage(imagePath);
            } catch (InvalidPathException ignored) {
                System.out.println("Problem on loading File");
            }
        });
        SeparatorMenuItem sep = new SeparatorMenuItem();
        ContextMenu quickOptions = new ContextMenu();
        quickOptions.getItems().addAll(deleteMenu, selectMenu, sep, renameMenu);
        quickOptions.setOnAutoHide(event -> contextPlaylist.setName(nameField.getText()));

        mediaLibrary.addListener((ListChangeListener<? super Playlist>) change -> {
            tilePane.getChildren().clear();
            //System.out.println(mediaLibrary);
            for (Playlist playlist : mediaLibrary) {
                //System.out.println(playlist.getName());
                //System.out.println();
                Button playlistButton = new Button(playlist.getName());
                playlistButton.setMinWidth(Region.USE_PREF_SIZE);
                playlistButton.setWrapText(true);
                playlistButton.hoverProperty().addListener((obs, oldValue, newValue) -> {
                    if (newValue) {
                        playlistButton.setStyle("-fx-font-size:14");
                    } else {
                        playlistButton.setStyle("-fx-font-size:18");
                    }
                });
                //genau position beim auftauchen setzen
                playlistButton.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        contextPlaylist = playlist;
                        nameField.setText(contextPlaylist.getName());
                    }
                });
                playlistButton.setContextMenu(quickOptions);

                playlistButton.setOnAction((e) -> {
                    GenericView view = screenController.activateWindow(SongView.class, true);
                    if (view instanceof SongView songView) {
                        songView.setPlaylist(playlist, true);
                    }
                });
                if (playlist.getPreviewImage() != null && playlistButton.graphicProperty().get() == null) {
                    showPlaylistImage(playlistButton, playlist.getPreviewImage());
                }
                playlist.getPreviewImageProperty().addListener((observableValue, oldString, newString) ->
                        showPlaylistImage(playlistButton, newString)
                );

                playlist.getNameProperty().addListener((observableValue, oldString, newString) ->
                        playlistButton.setText(newString)
                );
                playlistButton.setAlignment(Pos.BASELINE_CENTER);
                playlistButton.setStyle("-fx-font-size:20");
                playlistButton.setPrefHeight(100);
                playlistButton.setPrefWidth(175);
                tilePane.getChildren().add(playlistButton);
            }
        });

        mediaLibrary.addAll(SettingFile.load().getMediaLibrary());

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

        //TODO verbuggt beim mehrmaligen aktivieren?
        Button automaticPlaylistButton = new Button("Playlist Vorschläge");
        automaticPlaylistButton.setMinWidth(Control.USE_PREF_SIZE);
        automaticPlaylistButton.setOnAction(e -> createAutomaticPlaylists());

        vbox.getChildren().addAll(welcomeLabel, sp, musicPlayerButton, automaticPlaylistButton);
        showNodes(vbox);

        setDestroyListener(() -> SettingFile.setMediaLibrary(mediaLibrary));
    }

    public boolean addPlaylist(Playlist createdPlaylist) {
        //damit nicht mehrere Playlisten selben inhalts erstellt werden
        if (mediaLibrary.contains(createdPlaylist)) {
            return false;
        }
        //for cloning playlist in mainView can change without interacting with the saved playlists in the media library
        System.out.println("added a new playlist " + createdPlaylist.getName());
        Playlist copyPlaylist = new Playlist(createdPlaylist);
        mediaLibrary.add(copyPlaylist);
        //SettingFile.setMediaLibrary(mediaLibrary);
        return true;
    }

    public boolean deletePlaylist(Playlist playlist) {
        if (mediaLibrary.contains(playlist)) {
            return false;
        }
        mediaLibrary.remove(playlist);
        return true;
    }

    private void createAutomaticPlaylists() {
        int threshold = 10;
        //Criteria: genre and artist
        ArrayList<String> genreCriteria = new ArrayList<>();
        ArrayList<String> artistCriteria = new ArrayList<>();
        for (int i = 0; i < allSongs.size(); i++) {
            if (!(genreCriteria.contains(allSongs.get(i).getGenre())) && !(allSongs.get(i).getGenre().equals(""))) {
                genreCriteria.add(allSongs.get(i).getGenre());
            } else if (!(artistCriteria.contains(allSongs.get(i).getArtist())) && !(allSongs.get(i).getArtist().equals(""))) {
                artistCriteria.add(allSongs.get(i).getArtist());
            }
        }
        //System.out.println(genreCriteria + "\n" + artistCriteria);

        for (String genre:genreCriteria) {
            allSongs.setPredicate(p -> p.getGenre().contains(genre));
            if (allSongs.size() >= threshold) {
                Playlist automaticPlaylist = new Playlist();
                automaticPlaylist.setName("Genre: " + genre);
                for (Song song : allSongs) {
                    automaticPlaylist.add(song);
                }
                addPlaylist(automaticPlaylist);
            }
        }
        allSongs.setPredicate(null);
        for (String artist:artistCriteria) {
            allSongs.setPredicate(p -> p.getArtist().contains(artist));
            if (allSongs.size() >= threshold) {
                Playlist automaticPlaylist = new Playlist();
                automaticPlaylist.setName("Artist: " + artist);
                for (Song song : allSongs) {
                    automaticPlaylist.add(song);
                }
                addPlaylist(automaticPlaylist);
            }
        }
        allSongs.setPredicate(null);
    }
    //nicht ideal die methode aber muss einmal am anfang und im listener eingesetzt werden
    private void showPlaylistImage(Button playlistButton, Path path) {
        Image previewImage = new Image(path.toUri().toString(), true);
        System.out.println(previewImage.errorProperty());
        if (!previewImage.isError()) {
            ImageView previewView = new ImageView(previewImage);
            System.out.println("new image " + path);
            //\media\AlbumCover.jpg zum Beispiel
            previewView.setFitWidth(80);
            previewView.setFitHeight(80);
            //TODO preserve ratio or not? --> LW: yeeesss :)
            previewView.setPreserveRatio(true);
            playlistButton.setGraphic(previewView);
        }
    }
}
