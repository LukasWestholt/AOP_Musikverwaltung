package musikverwaltung;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.util.ArrayList;

public class MainView extends MenuBarView {
    public static final String HIGHLIGHT_START = "<HIGHLIGHT_START>";
    public static final String HIGHLIGHT_END = "<HIGHLIGHT_END>";

    MediaManager mediaManager = new MediaManager();
    TableView<Musikstueck> table = new TableView<>();

    public MainView(ScreenController sc) {
        super(sc);

        addActiveMenuButton(settingButton,
                e -> screenController.activateWindow("Einstellungen", false, 350, 300)
                        .clearActionListener().addActionListener(() -> mediaManager.clearAndLoadAll(table::refresh))
        );
        addActiveMenuButton(playlistButton,
                e -> screenController.activate("Playlist")
        );
        setActiveMenuItem(mainViewButton);
    }

    // https://stackoverflow.com/a/47560767/8980073
    public void prepare() {
        mediaManager.clearAndLoadAll(table::refresh);

        FilteredList<Musikstueck> flMusikstueck = new FilteredList<>(mediaManager.music, p -> true); //Pass the data to a filtered list

        final Label welcomeLabel = new Label("Willkommen in der Musikverwaltung");
        welcomeLabel.setFont(new Font("Arial", 20));

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(Control.USE_PREF_SIZE);
        deleteButton.setStyle("-fx-text-fill: #ef0505");
        Label actionLabel = new Label();
        actionLabel.setAlignment(Pos.CENTER);
        actionLabel.setMaxWidth(Double.MAX_VALUE);
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
            switch (choiceBox.getValue())
            {
                //filter table by one key
                case "Überall" ->
                        flMusikstueck.setPredicate(p -> p.search_everywhere(newValue));
                case "Titel" ->
                        flMusikstueck.setPredicate(p -> p.bekommePrimaryKey().toLowerCase().contains(newValue.toLowerCase().trim()));
                case "Interpret" ->
                        flMusikstueck.setPredicate(p -> p.bekommeInterpret().toLowerCase().contains(newValue.toLowerCase().trim()));
                case "Genre" ->
                        flMusikstueck.setPredicate(p -> p.bekommeGenre().toLowerCase().contains(newValue.toLowerCase().trim()));
            }
        });

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
            screenController.activateWindow("Player", true, 220, 0);
        });
        HBox searchHBox = new HBox(choiceBox, textSearchField, musicPlayerButton);//Add choiceBox and textField to hBox
        searchHBox.setAlignment(Pos.CENTER);//Center HBox

        TableColumn<Musikstueck, String> titleCol = new TableColumn<>("Titel");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().bekommeHighlightedPrimaryKey(textSearchField.getText())
        ));
        titleCol.setCellFactory(highlightedTableCell());

        TableColumn<Musikstueck, String> interpretCol = new TableColumn<>("Interpret");
        interpretCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().bekommeHighlightedInterpret(textSearchField.getText())
        ));
        interpretCol.setCellFactory(highlightedTableCell());

        TableColumn<Musikstueck, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().bekommeHighlightedGenre(textSearchField.getText())
        ));
        genreCol.setCellFactory(highlightedTableCell());

        titleCol.prefWidthProperty().bind(table.widthProperty().divide(3).subtract(15)); // Quick fix for not showing the horizontal scroll bar.
        interpretCol.prefWidthProperty().bind(table.widthProperty().divide(3));
        genreCol.prefWidthProperty().bind(table.widthProperty().divide(3));

        table.setItems(flMusikstueck); //Set the table's items using the filtered list
        table.getColumns().add(titleCol);
        table.getColumns().add(interpretCol);
        table.getColumns().add(genreCol);

        table.setRowFactory( tv -> {
            TableRow<Musikstueck> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    actionLabel.setText("Spiele ab...");
                    ArrayList<Musikstueck> playlist = new ArrayList<>();
                    playlist.add(row.getItem());
                    GenericView view = screenController.activateWindow("Player", true, 220, 0);
                    if (view instanceof SongView songView) {
                        songView.setPlaylist(playlist);
                    }
                }
            });
            return row;
        });

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

        StackPane.setAlignment(vbox, Pos.TOP_LEFT);
        StackPane.setAlignment(rectangle, Pos.TOP_LEFT);
        showNodes(rectangle, vbox);
    }

    // https://stackoverflow.com/q/26906810/8980073
    private Callback<TableColumn<Musikstueck, String>, TableCell<Musikstueck, String>> highlightedTableCell() {
        return new Callback<>() {
            @Override
            public TableCell<Musikstueck, String> call(TableColumn param) {
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
