package musikverwaltung.views;


import java.io.*;
import java.util.ArrayList;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import musikverwaltung.ScreenController;
import musikverwaltung.SettingFile;

public class SettingsView extends GenericView implements Serializable {
    final ObservableList<String> list = FXCollections.observableArrayList();
    File directory;

    public SettingsView(ScreenController sc) {
        super(sc, 350, 300);

        DirectoryChooser directoryChooser = new DirectoryChooser();

        directoryChooser.setInitialDirectory(directory);

        Button selectDirectory = new Button("Select Directory");
        selectDirectory.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory != null) {
                list.add(selectedDirectory.getAbsolutePath());
            }
        });
        ListView<String> listDirectory = new ListView<>(list);
        listDirectory.setCellFactory(param -> new XCell(getWidthProperty()));
        listDirectory.setFocusTraversable(false);
        VBox.setVgrow(listDirectory, Priority.ALWAYS);

        Button buttonSave = new Button("Save");
        buttonSave.setOnAction(e -> {
            SettingFile.save(new ArrayList<>(list));
            stage.close();
            triggerActionListener();
        });
        Button buttonCancel = new Button("Cancel");
        buttonCancel.setCancelButton(true);
        buttonCancel.setOnAction(e -> stage.close());
        HBox buttonHBox = new HBox(buttonSave, buttonCancel);
        VBox settingsVBox = new VBox(selectDirectory, listDirectory, buttonHBox);
        showNodes(settingsVBox);
    }

    @Override
    public Node get() {
        list.setAll(SettingFile.load());

        directory = new File(System.getProperty("user.home"));
        if (!directory.exists()) {
            directory = new File(".");
        }
        return super.get();
    }

    static class XCell extends ListCell<String> {
        final HBox hbox = new HBox();
        final Label label = new Label("");
        final Button button = new Button("(Del)");

        XCell(DoubleBinding widthProperty) {
            super();
            button.setMinWidth(Control.USE_PREF_SIZE);
            button.setOnAction(event -> getListView().getItems().remove(getItem()));
            HBox.setMargin(button, new Insets(0, 5, 0, 0));
            label.prefWidthProperty().bind(widthProperty.subtract(button.widthProperty()).subtract(25));
            label.setWrapText(true);
            hbox.setSpacing(4);
            hbox.getChildren().addAll(label, button);
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
