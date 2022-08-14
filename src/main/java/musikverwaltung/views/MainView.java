package musikverwaltung.views;

import java.util.function.Predicate;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.When;
import javafx.beans.property.*;
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
import musikverwaltung.ScreenController;
import musikverwaltung.data.Playlist;
import musikverwaltung.data.Song;
import musikverwaltung.handler.RefreshListener;
import musikverwaltung.handler.SetActionLabelListener;


public class MainView extends MenuBarView implements SetActionLabelListener, RefreshListener {
    public static final String HIGHLIGHT_START = "<HIGHLIGHT_START>";
    public static final String HIGHLIGHT_END = "<HIGHLIGHT_END>";

    private final TableView<Song> table = new TableView<>();
    private final FilteredList<Song> flSong;
    final MediaManager mediaManager;
    final Label welcomeLabel;
    final Label actionLabel;
    final StackPane customButtonPane = new StackPane();
    public final ObjectProperty<Predicate<Song>> songFilterForPlaylist = new SimpleObjectProperty<>();

    private final SimpleBooleanProperty showPlaylistAdd = new SimpleBooleanProperty();

    // https://stackoverflow.com/a/47560767/8980073
    public MainView(ScreenController sc, MediaManager mediaManager, boolean includeUnplayableSongs) {
        super(sc);

        this.mediaManager = mediaManager;

        addActiveMenuButton(settingViewButton,
                e -> {
                    final GenericView view = screenController.activateWindow(SettingsView.class, false);
                    if (view instanceof SettingsView) {
                        SettingsView settingsView = (SettingsView) view;
                        settingsView.listenerInitiator.addListenerIfNotContains(this);
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
        refresh().run();

        songFilterForPlaylist.bind(Bindings.createObjectBinding(() -> song -> true));
        ObjectProperty<Predicate<Song>> userFilter = new SimpleObjectProperty<>();
        userFilter.bind(Bindings.createObjectBinding(() -> song -> true));

        //Pass the data to a filtered list
        // TODO is the new FilteredList here necessary?
        flSong = new FilteredList<>(mediaManager.music.filtered(s -> includeUnplayableSongs || s.isPlayable()));
        flSong.predicateProperty().bind(Bindings.createObjectBinding(
                () -> songFilterForPlaylist.get().and(userFilter.get()),
                songFilterForPlaylist, userFilter));

        welcomeLabel = new Label("Willkommen in der Musikverwaltung");
        welcomeLabel.getStyleClass().add("header");

        actionLabel = new Label();
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
        Button reloadButton = new Button("Neuladen");
        reloadButton.setMinWidth(Control.USE_PREF_SIZE);
        reloadButton.setOnAction(e -> {
            actionLabel.setText("Reload");
            refresh().run();
            // TODO bug: "Playlist erstellen" button geht nicht weg
        });

        Button selectAll = new Button("alle auswählen");
        selectAll.setMinWidth(Control.USE_PREF_SIZE);
        selectAll.setOnAction(e -> {
            boolean wasAllSelect = true;
            for (int i = 0; i < flSong.size(); i++) {
                Song song = flSong.get(i);
                if (!song.isSelected()) {
                    wasAllSelect = false;
                    // TODO quick-and-dirty
                    song.select(new SimpleIntegerProperty(i));
                }
            }
            if (wasAllSelect) {
                for (Song song : flSong) {
                    song.deselect();
                }
                showPlaylistAdd.set(false);
            } else {
                showPlaylistAdd.set(true);
            }
            table.refresh(); // TODO ist das für weirden übergang verantwortlich?
        });

        HBox menu = new HBox(customButtonPane, actionLabel, selectAll, reloadButton);
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(5);

        //ChoiceBox and TextField for searching
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setMinWidth(Control.USE_PREF_SIZE);
        choiceBox.getItems().addAll("Überall", "Titel", "Interpret", "Genre");
        choiceBox.setValue("Überall");

        TextField textSearchField = new TextField();
        textSearchField.setPromptText("Search here!");
        userFilter.bind(Bindings.createObjectBinding(
                () -> s -> (generateUserFilter(choiceBox, s, textSearchField)).get(),
                textSearchField.textProperty()));

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            //reset table and text-field when new choice is selected
            if (newVal != null) {
                textSearchField.setText("");
            }
        });
        Button musicPlayerButton = new Button("Player");
        musicPlayerButton.setMinWidth(Control.USE_PREF_SIZE);
        musicPlayerButton.setOnAction(e -> {
            actionLabel.setText("Starte Player");
            final GenericView view = screenController.activateWindow(SongView.class, true);
            if (view instanceof SongView) {
                SongView songView = (SongView) view;
                songView.listenerInitiator.addListenerIfNotContains(this);
                Song lastSong = mediaManager.getPlayableLastSong();
                songView.setPlaylist(lastSong, false);

            }
        });
        HBox searchHBox = new HBox(choiceBox, textSearchField, musicPlayerButton); //Add choiceBox and textField to hBox
        searchHBox.setAlignment(Pos.CENTER); //Center HBox

        TableColumn<Song, Boolean> checkCol = new TableColumn<>();
        checkCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(
                cellData.getValue().isSelected()
        ));
        checkCol.setCellFactory(param -> new CheckboxCell());

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
                    final GenericView view = screenController.activateWindow(SongView.class, true);
                    if (view instanceof SongView) {
                        SongView songView = (SongView) view;
                        songView.listenerInitiator.addListenerIfNotContains(this);
                        songView.setPlaylist(row.getItem(), true);
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
                returnPlaylist.setAll(getSelectedSongs());
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

    private BooleanBinding generateUserFilter(ChoiceBox<String> choiceBox, Song song, TextField textField) {
        String search = textField.getText().toLowerCase().trim();
        return new When(choiceBox.valueProperty().isEqualTo("Überall"))
                .then(song.searchEverywhere(search)).otherwise(
                    new When(choiceBox.valueProperty().isEqualTo("Titel"))
                .then(song.getPrimaryKey().toLowerCase().contains(search)).otherwise(
                    new When(choiceBox.valueProperty().isEqualTo("Interpret"))
                .then(song.getArtist().toLowerCase().contains(search)).otherwise(
                    new When(choiceBox.valueProperty().isEqualTo("Genre"))
                .then(song.getGenre().toLowerCase().contains(search)).otherwise(true)
                )));
    }

    public FilteredList<Song> getSelectedSongs() {
        return flSong.filtered(Song::isSelected);
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

    @Override
    public void setActionLabel(String text) {
        actionLabel.setText(text);
    }

    class CheckboxCell extends TableCell<Song, Boolean> {
        private final CheckBox checkBox = new CheckBox();

        public CheckboxCell() {
            super();
            checkBox.setOnAction(action -> {
                final TableRow<Song> row = this.getTableRow();
                final Song song = row.getItem();
                if (song.isSelected()) {
                    song.deselect();
                } else {
                    song.select(row.indexProperty());
                }
                showPlaylistAdd.set(!getSelectedSongs().isEmpty());
            });
        }

        @Override
        protected void updateItem(Boolean isSelected, boolean empty) {
            super.updateItem(isSelected, empty);
            if (!empty && isSelected != null) {
                checkBox.setSelected(isSelected);
                setGraphic(checkBox);
            } else {
                setGraphic(null);
            }
        }
    }
}
