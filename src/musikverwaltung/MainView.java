package musikverwaltung;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class MainView extends GenericView {
    private final ObservableList<Musikstueck> data
            = FXCollections.observableArrayList(
            new Musikstueck("Atemlos", "Helene Fischer", "Schlager")
    );
    public static final String HIGHLIGHT_START = "<HIGHLIGHT_START>";
    public static final String HIGHLIGHT_END = "<HIGHLIGHT_START>";

    public MainView(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        super(width, height);
    }

    // https://stackoverflow.com/a/47560767/8980073
    public StackPane get() {
        FilteredList<Musikstueck> flMusikstueck = new FilteredList<>(data, p -> true);//Pass the data to a filtered list

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
        saveButton.setOnAction(e -> actionLabel.setText("Save button pressed"));

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
                        flMusikstueck.setPredicate(p -> p.bekommeTitel().toLowerCase().contains(newValue.toLowerCase().trim()));
                case "Interpret" ->
                        flMusikstueck.setPredicate(p -> p.bekommeInterpret().toLowerCase().contains(newValue.toLowerCase().trim()));
                case "Genre" ->
                        flMusikstueck.setPredicate(p -> p.bekommeGenre().toLowerCase().contains(newValue.toLowerCase().trim()));
            }
        });

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            //reset table and textfield when new choice is selected
            if (newVal != null) {
                textSearchField.setText("");
            }
        });

        HBox searchHBox = new HBox(choiceBox, textSearchField);//Add choiceBox and textField to hBox
        searchHBox.setAlignment(Pos.CENTER);//Center HBox

        TableColumn<Musikstueck, String> titleCol = new TableColumn<>("Titel");
        titleCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().bekommeHighlighted(cellData.getValue().bekommeTitel(), textSearchField.getText())
        ));
        titleCol.setCellFactory(highlightedTableCell());

        TableColumn<Musikstueck, String> interpretCol = new TableColumn<>("Interpret");
        interpretCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().bekommeHighlighted(cellData.getValue().bekommeInterpret(), textSearchField.getText())
        ));
        interpretCol.setCellFactory(highlightedTableCell());

        TableColumn<Musikstueck, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().bekommeHighlighted(cellData.getValue().bekommeGenre(), textSearchField.getText())
        ));
        /*genreCol.setCellValueFactory(
                new PropertyValueFactory<>("bekommeGenre"));*/
        genreCol.setCellFactory(highlightedTableCell());

        TableView<Musikstueck> table = new TableView<>();

        titleCol.prefWidthProperty().bind(table.widthProperty().divide(3).subtract(3.5)); // Quick fix for not showing the horizontal scroll bar.
        interpretCol.prefWidthProperty().bind(table.widthProperty().divide(3));
        genreCol.prefWidthProperty().bind(table.widthProperty().divide(3));

        table.setEditable(true);
        table.setItems(flMusikstueck); //Set the table's items using the filtered list
        table.getColumns().add(titleCol);
        table.getColumns().add(interpretCol);
        table.getColumns().add(genreCol);


        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(welcomeLabel, menu, table, searchHBox);

        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(scene_width);
        rectangle.heightProperty().bind(scene_height);
        rectangle.setFill(new LinearGradient(
                0, 0, 1, 1, true, //sizing
                CycleMethod.NO_CYCLE, //cycling
                new Stop(0, Color.web("#81c483")), //colors
                new Stop(1, Color.web("#fcc200")))
        );

        /*rectangle.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("rectangle");
            System.out.println(newVal);
        });*/

        StackPane.setAlignment(vbox, Pos.TOP_LEFT);
        StackPane.setAlignment(rectangle, Pos.TOP_LEFT);
        stackPane.getChildren().addAll(rectangle, vbox);
        return stackPane;
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
                        if (!empty && text.contains(HIGHLIGHT_START)) {
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
                        } else if (!empty) {
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

    public void setSceneHeight(ReadOnlyDoubleProperty height) {
        scene_height = height;
    }
    public void setSceneWidth(ReadOnlyDoubleProperty width) {
        scene_width = width;
    }
}
