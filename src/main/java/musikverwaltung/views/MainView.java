package musikverwaltung.views;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.function.Consumer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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
    private final Playlist playList = new Playlist();
    final MediaManager mediaManager;

    // https://stackoverflow.com/a/47560767/8980073
    public MainView(ScreenController sc, MediaManager mediaManager) {
        super(sc);

        this.mediaManager = mediaManager;

        Runnable uniqueRefreshRunnable = refresh();
        addActiveMenuButton(settingButton,
                e -> {
                    GenericView view = screenController.activateWindow(SettingsView.class, false);
                    if (view instanceof SettingsView settingsView) {
                        settingsView.addListenerIfNotContains(uniqueRefreshRunnable);
                    }
                }
        );
        addActiveMenuButton(playlistButton,
                e -> screenController.activate(PlaylistView.class)
        );
        setActiveMenuItem(mainViewButton);
        uniqueRefreshRunnable.run();

        //Pass the data to a filtered list
        final FilteredList<Song> flSong = new FilteredList<>(mediaManager.music, p -> true);
        System.out.println(flSong);
        System.out.println();
        System.out.println();

        final Label welcomeLabel = new Label("Willkommen in der Musikverwaltung");
        welcomeLabel.getStyleClass().add("header");

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(Control.USE_PREF_SIZE);
        deleteButton.setStyle("-fx-text-fill: #ef0505");
        Label actionLabel = new Label();
        actionLabel.setAlignment(Pos.CENTER);
        actionLabel.setMaxWidth(Double.MAX_VALUE);
        PauseTransition pause = new PauseTransition(Duration.seconds(20));
        pause.setOnFinished(e -> {
            System.out.println("clear");
            actionLabel.setText("");
        });
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

        HBox menu = new HBox(deleteButton, actionLabel, saveButton);
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
                case "Überall" -> flSong.setPredicate(p ->
                        p.search_everywhere(newValue));
                case "Titel" -> flSong.setPredicate(p ->
                        p.getPrimaryKey().toLowerCase().contains(newValue.toLowerCase().trim())
                );
                case "Interpret" -> flSong.setPredicate(p ->
                        p.getArtist().toLowerCase().contains(newValue.toLowerCase().trim())
                );
                case "Genre" -> flSong.setPredicate(p ->
                        p.getGenre().toLowerCase().contains(newValue.toLowerCase().trim())
                );
                default -> throw new InputMismatchException("");
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
            GenericView view = screenController.activateWindow(SongView.class, true);
            if (view instanceof SongView songView) {
                songView.addListenerIfNotContains(setActionText);
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

        TableColumn<Song, Boolean> checkCol = new TableColumn<>("   ");
        //checkCol.setCellValueFactory( f -> f.getValue().getCompleted());
        //checkCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue()));
        // checkCol.setCellFactory(ft -> new CheckBoxTableCell<>());
        /*checkCol.setCellFactory(new Callback<TableColumn<Song, Boolean>, TableCell<Song, Boolean>>() {
            @Override
            public TableCell<Song, Boolean> call(TableColumn<Song, Boolean> songBooleanTableColumn) {
                return new CheckBoxTableCell<>();
            }
        });
        checkCol.setCellFactory(new CheckBoxTableCell(Callback<Integer,ObservableValue<Boolean>> getSelectedProperty));
        */


        //TODO !!!! WEG 1: besser aber es werden andere weiter noch random selected
        checkCol.setCellFactory(
                new Callback<TableColumn<Song, Boolean>, TableCell<Song, Boolean>>() {
                    public TableCell<Song, Boolean> call(TableColumn p) {
                        return new TableCell<Song, Boolean>() {
                            private final CheckBox checkBox = new CheckBox();


                            @Override
                            public void updateItem(Boolean item, boolean empty) {
                                super.updateItem(item, empty);

                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                                        @Override
                                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                            //getTableView().requestFocus();
                                            Song selectedSong = getTableView().getItems().get(getIndex());
                                            if (newValue) {
                                                playList.add(selectedSong);
                                            } else {
                                                getTableView().getSelectionModel().clearSelection(getIndex());
                                                if (playList.contains(selectedSong)) {
                                                    playList.remove(selectedSong);
                                                }

                                            }
                                        }
                                    });
                                }
                            }
                        };
                    }
                });
        //TODO WEG 2: Liste hat nicht die erwartete Größe von 13 -> bugged
        /*
        ArrayList<CheckBox> C_LIST = new ArrayList<>();
        ArrayList<Song> S_LIST = new ArrayList<>();
        TableColumn<Song, CheckBox> checkCol = new TableColumn<>("   ");
        checkCol.setCellValueFactory(new Callback<>() {

            @Override
            public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<Song, CheckBox> arg0) {

                CheckBox checkBox = new CheckBox();
                Song selectedSong = arg0.getValue();
                if (S_LIST.contains(selectedSong)) {
                    C_LIST.remove(S_LIST.indexOf(selectedSong));
                    S_LIST.remove(selectedSong);
                    C_LIST.add(checkBox);
                    S_LIST.add(selectedSong);
                } else {
                    C_LIST.add(checkBox);
                    S_LIST.add(selectedSong);
                }

                checkBox.selectedProperty().addListener(new ChangeListener<>() {
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                        Song song = S_LIST.get(C_LIST.indexOf(checkBox));

                        if (newVal) {
                            playList.add(song);
                        } else {
                            if (playList.contains(song)) {
                                playList.remove(song);
                            }
                        }
                        System.out.println(playList.getSongs());
                    }
                });
                return new SimpleObjectProperty<>(checkBox);

            }

        });*/

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

        // Quick fix for not showing the horizontal scroll bar.
        checkCol.prefWidthProperty().bind(table.widthProperty().divide(10));
        titleCol.prefWidthProperty().bind(table.widthProperty().divide(3).subtract(15));
        interpretCol.prefWidthProperty().bind(table.widthProperty().divide(3));
        genreCol.prefWidthProperty().bind(table.widthProperty().divide(3));

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
                    GenericView view = screenController.activateWindow(SongView.class, true);
                    if (view instanceof SongView songView) {
                        songView.addListenerIfNotContains(setActionText);
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


        Button makePlaylistButton = new Button("Playlist erstellen");
        makePlaylistButton.setOnAction(action -> {
            GenericView view = screenController.activateWindow(PlaylistView.class, true);
            if (view instanceof PlaylistView playlistView) {
                System.out.println(playList.getSongs());
                if (!playList.isEmpty()) {
                    playlistView.addPlaylist(playList);
                    System.out.println("moved playlist to next page");
                }
            }

        });

        TextField playlistNameEntry = new TextField("Playlist 1");
        playlistNameEntry.textProperty().addListener((useless, oldValue, newValue) -> {
            System.out.println(playlistNameEntry.getText());
            playList.setName(playlistNameEntry.getText());
        });

        final HBox hbox = new HBox(makePlaylistButton, playlistNameEntry);
        /*playlist.addListener(new ListChangeListener<Song>() {
            @Override
            public void onChanged(Change<? extends Song> change) {
                System.out.println(change);
                if (!playlist.isEmpty() && !vbox.getChildren().contains(hbox)) {
                   // vbox.getChildren().add(makePlaylistButton);
                    vbox.getChildren().add(hbox);
                }
                if (playlist.isEmpty()) {
                    vbox.getChildren().remove(hbox);
                }
            }
        });*/

        //Gibt einem bei Auswahl von Songs die Möglichkeit Playlists zu erstellen und zu benennen
        playList.getSongs().addListener((ListChangeListener<Song>) change -> {
            System.out.println(change);
            if (!playList.isEmpty() && !vbox.getChildren().contains(hbox)) {
                vbox.getChildren().add(hbox);
            }
            if (playList.isEmpty()) {
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
