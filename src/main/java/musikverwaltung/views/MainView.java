package musikverwaltung.views;

import java.util.InputMismatchException;
import java.util.function.Consumer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;
import musikverwaltung.MediaManager;
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;
import musikverwaltung.Song;


public class MainView extends MenuBarView {
    public static final String HIGHLIGHT_START = "<HIGHLIGHT_START>";
    public static final String HIGHLIGHT_END = "<HIGHLIGHT_END>";

    final TableView<Song> table = new TableView<>();
    private final Playlist playList = new Playlist(); // TODO not needed?
    final MediaManager mediaManager;

    // https://stackoverflow.com/a/47560767/8980073
    public MainView(ScreenController sc, MediaManager mediaManager) {
        super(sc);

        this.mediaManager = mediaManager;

        Runnable uniqueRefreshRunnable = refresh();
        addActiveMenuButton(settingViewButton,
                e -> {
                    final GenericView view = screenController.activateWindow(SettingsView.class, false);
                    if (view instanceof SettingsView) {
                        SettingsView settingsView = (SettingsView) view;
                        settingsView.addActionListenerIfNotContains(uniqueRefreshRunnable);
                    }
                }
        );
        addActiveMenuButton(playlistViewButton,
                e -> screenController.activate(PlaylistView.class)
        );
        addActiveMenuButton(creditsViewButton,
                e -> screenController.activateWindow(CreditsView.class, false)
        );
        setActiveMenuItem(mainViewButton);
        uniqueRefreshRunnable.run();

        //Pass the data to a filtered list
        final FilteredList<Song> flSong = new FilteredList<>(mediaManager.music, p -> true);
        final SimpleBooleanProperty showPlaylistAdd = new SimpleBooleanProperty();

        final Label welcomeLabel = new Label("Willkommen in der Musikverwaltung");
        welcomeLabel.getStyleClass().add("header");

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(Control.USE_PREF_SIZE);
        deleteButton.setStyle("-fx-text-fill: #ef0505");
        Label actionLabel = new Label();
        actionLabel.setAlignment(Pos.CENTER);
        actionLabel.setMaxWidth(Double.MAX_VALUE);
        PauseTransition pause = new PauseTransition(Duration.seconds(20));
        pause.setOnFinished(e -> actionLabel.setText(""));
        actionLabel.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                pause.playFromStart();
            }
        });
        HBox.setHgrow(actionLabel, Priority.ALWAYS);
        Button saveButton = new Button("Speichern");
        saveButton.setMinWidth(Control.USE_PREF_SIZE);
        saveButton.setOnAction(e -> {
            actionLabel.setText("Save button pressed");
            table.refresh();
        });

        Button selectAll = new Button("alle auswählen");
        selectAll.setMinWidth(Control.USE_PREF_SIZE);
        selectAll.setOnAction(e -> {
            boolean allUnselect = true;
            for (Song song : flSong) {
                if (!song.isSelected()) {
                    song.setSelected(true);
                    allUnselect = false;
                }
            }
            if (allUnselect) {
                for (Song song : flSong) {
                    song.setSelected(false);
                }
                showPlaylistAdd.set(false);
            } else {
                showPlaylistAdd.set(true);
            }
            table.refresh(); // TODO not needed right? Doch für das update der checkboxen visuals
        });

        HBox menu = new HBox(deleteButton, actionLabel, selectAll, saveButton);
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(5);

        //ChoiceBox and TextField for searching
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setMinWidth(Control.USE_PREF_SIZE);
        choiceBox.getItems().addAll("Überall", "Titel", "Interpret", "Genre");
        choiceBox.setValue("Überall");

        TextField textSearchField = new TextField();
        textSearchField.setPromptText("Search here!");
        textSearchField.textProperty().addListener((obs, oldValue, newValue) -> {
            switch (choiceBox.getValue()) {
                //filter table by one key
                case "Überall":
                    flSong.setPredicate(p -> p.search_everywhere(newValue));
                    break;
                case "Titel":
                    flSong.setPredicate(p -> p.getPrimaryKey().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Interpret":
                    flSong.setPredicate(p -> p.getArtist().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                case "Genre":
                    flSong.setPredicate(p -> p.getGenre().toLowerCase().contains(newValue.toLowerCase().trim()));
                    break;
                default:
                    throw new InputMismatchException("");
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            //reset table and text-field when new choice is selected
            if (newVal != null) {
                textSearchField.setText("");
            }
        });
        Consumer<String> setActionText = actionLabel::setText;
        Button musicPlayerButton = new Button("Player");
        musicPlayerButton.setMinWidth(Control.USE_PREF_SIZE);
        musicPlayerButton.setOnAction(e -> {
            actionLabel.setText("Starte Player");
            final GenericView view = screenController.activateWindow(SongView.class, true);
            if (view instanceof SongView) {
                SongView songView = (SongView) view;
                songView.addStringListenerIfNotContains(setActionText);
                Song lastSong = mediaManager.getLastSong();
                if (lastSong != null) {
                    Playlist singleSongPlaylist = new Playlist();
                    singleSongPlaylist.add(lastSong);
                    singleSongPlaylist.setName(lastSong.getTitle());
                    songView.setPlaylist(singleSongPlaylist, false);
                }

            }
        });
        HBox searchHBox = new HBox(choiceBox, textSearchField, musicPlayerButton); //Add choiceBox and textField to hBox
        searchHBox.setAlignment(Pos.CENTER); //Center HBox

        TableColumn<Song, CheckBox> checkCol = new TableColumn<>();
        checkCol.setCellValueFactory(cellData -> {
            CheckBox checkBox = new CheckBox();
            Song song = cellData.getValue();
            checkBox.setSelected(song.isSelected());
            checkBox.setOnAction(action -> {
                song.setSelected(!song.isSelected());
                showPlaylistAdd.set(!new FilteredList<>(flSong, Song::isSelected).isEmpty());
            });
            return new SimpleObjectProperty<>(checkBox);
        });

        TableColumn<Song, String> titleCol = new TableColumn<>("Titel");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getHighlightedPrimaryKey(textSearchField.getText())
        ));
        titleCol.setCellFactory(highlightedTableCell());

        TableColumn<Song, String> interpretCol = new TableColumn<>("Interpret");
        interpretCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getHighlightedArtist(textSearchField.getText())
        ));
        interpretCol.setCellFactory(highlightedTableCell());

        TableColumn<Song, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getHighlightedGenre(textSearchField.getText())
        ));
        genreCol.setCellFactory(highlightedTableCell());

        checkCol.setPrefWidth(40);
        titleCol.prefWidthProperty().bind(table.widthProperty().divide(3));
        interpretCol.prefWidthProperty().bind(table.widthProperty().divide(3));
        // Quick fix for not showing the horizontal scroll bar.
        genreCol.prefWidthProperty().bind(table.widthProperty().divide(3).subtract(40).subtract(40));

        table.setItems(flSong); //Set the table's items using the filtered list
        table.getColumns().add(checkCol);
        table.getColumns().add(titleCol);
        table.getColumns().add(interpretCol);
        table.getColumns().add(genreCol);

        table.setRowFactory(tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Playlist singleSongPlaylist = new Playlist();
                    singleSongPlaylist.add(row.getItem());
                    singleSongPlaylist.setName(row.getItem().getTitle());
                    final GenericView view = screenController.activateWindow(SongView.class, true);
                    if (view instanceof SongView) {
                        SongView songView = (SongView) view;
                        songView.addStringListenerIfNotContains(setActionText);
                        songView.setPlaylist(singleSongPlaylist, true);
                    }
                }
            });
            return row;
        });
        VBox.setVgrow(table, Priority.ALWAYS);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(welcomeLabel, menu, table, searchHBox);

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(getWidthProperty());
        rectangle.heightProperty().bind(getHeightProperty());
        rectangle.setFill(new LinearGradient(
                0, 0, 1, 1, true, //sizing
                CycleMethod.NO_CYCLE, //cycling
                new Stop(0, Color.web("#81c483")), //colors
                new Stop(1, Color.web("#fcc200")))
        );

        TextField playlistNameEntry = new TextField("Playlist 1");

        Button makePlaylistButton = new Button("Playlist erstellen");
        makePlaylistButton.setOnAction(action -> {
            // TODO save playlist to file and don't activate PlaylistView each
            final GenericView view = screenController.activate(PlaylistView.class);
            if (view instanceof PlaylistView) {
                Playlist returnPlaylist = new Playlist();
                returnPlaylist.setName(playlistNameEntry.getText());
                returnPlaylist.setAll(new FilteredList<>(flSong, Song::isSelected));
                if (!returnPlaylist.isEmpty()) {
                    PlaylistView playlistView = (PlaylistView) view;
                    playlistView.addPlaylist(returnPlaylist);
                    System.out.println("playlist added: " + returnPlaylist.getAll());
                    actionLabel.setText("Playlist with " + returnPlaylist.size() + " items added");
                }
            }
        });

        final HBox hbox = new HBox(makePlaylistButton, playlistNameEntry);

        //Gibt einem bei Auswahl von Songs die Möglichkeit Playlists zu erstellen und zu benennen
        showPlaylistAdd.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                vbox.getChildren().add(hbox);
            } else {
                vbox.getChildren().remove(hbox);
            }
        });

        StackPane.setAlignment(vbox, Pos.TOP_LEFT);
        StackPane.setAlignment(rectangle, Pos.TOP_LEFT);
        showNodes(rectangle, vbox);
    }

    public Runnable refresh() {
        return () -> mediaManager.clearAndLoadAll(table::refresh);
    }

    @Override
    public Node get() {
        Platform.runLater(refresh());
        return super.get();
    }

    // https://stackoverflow.com/q/26906810/8980073
    private Callback<TableColumn<Song, String>, TableCell<Song, String>> highlightedTableCell() {
        return new Callback<>() {
            @Override
            public TableCell<Song, String> call(TableColumn param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String text, boolean empty) {
                        super.updateItem(text, empty);
                        HBox hbox = new HBox();
                        if (!empty && text != null && text.contains(HIGHLIGHT_START)) {
                            // Something to highlight
                            while (text.contains(HIGHLIGHT_START)) {
                                // First part
                                Label label1 = new Label(text.substring(0, text.indexOf(HIGHLIGHT_START)));
                                hbox.getChildren().add(label1);
                                text = text.substring(text.indexOf(HIGHLIGHT_START) + HIGHLIGHT_START.length());
                                // Part to highlight
                                Label label2 = new Label(text.substring(0, text.indexOf(HIGHLIGHT_END)));
                                label2.setStyle("-fx-background-color: khaki;");
                                hbox.getChildren().add(label2);
                                // Last part
                                text = text.substring(text.indexOf(HIGHLIGHT_END) + HIGHLIGHT_END.length());
                                if (!text.contains(HIGHLIGHT_START)) {
                                    Label label3 = new Label(text);
                                    hbox.getChildren().add(label3);
                                }
                            }
                        } else if (!empty && text != null) {
                            // show simple text
                            Label label1 = new Label(text);
                            hbox.getChildren().add(label1);
                        }
                        setGraphic(hbox);
                    }
                };
            }
        };
    }
}
