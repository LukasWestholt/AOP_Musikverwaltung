package musikverwaltung;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.util.ArrayList;

public class SettingsView extends GenericView implements Serializable {
    ObservableList<String> list = FXCollections.observableArrayList();

    public SettingsView(ScreenController sc) {
        super(sc);
    }

    public void prepare() {
        list = FXCollections.observableArrayList(SettingFile.load());

        File directory = new File(System.getProperty("user.home"));
        if (!directory.exists()) {
            directory = new File(".");
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setInitialDirectory(directory);

        Button select_directory = new Button("Select Directory");
        select_directory.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                list.add(selectedDirectory.getAbsolutePath());
            }
        });
        ListView<String> list_directory = new ListView<>(list);
        list_directory.setCellFactory(param -> new XCell(getWidthProperty()));
        list_directory.setFocusTraversable(false);

        Button button_save = new Button("Save");
        button_save.setOnAction(e -> {
            SettingFile.save(new ArrayList<>(list));
            stage.close();
            triggerActionListener();
        });
        Button button_cancel = new Button("Cancel");
        button_cancel.setOnAction(e -> stage.close());
        HBox hBox = new HBox(button_save, button_cancel);
        VBox vBox = new VBox(select_directory, list_directory, hBox);
        showNodes(vBox);
    }

    static class XCell extends ListCell<String> {
        final HBox hbox = new HBox();
        final Label label = new Label("");
        final Pane pane = new Pane();
        final Button button = new Button("(Del)");

        public XCell(DoubleBinding widthProperty) {
            super();
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setMinWidth(Control.USE_PREF_SIZE);
            button.setOnAction(event -> getListView().getItems().remove(getItem()));
            label.prefWidthProperty().bind(widthProperty.subtract(button.widthProperty()).subtract(25));
            label.setWrapText(true);
            hbox.setSpacing(4);
            hbox.getChildren().addAll(label, pane, button);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                label.setText(null);
            } else {
                label.setText(item);
                setGraphic(hbox);
            }
        }
    }
}
