package musikverwaltung.views;

import java.nio.file.Path;
import java.util.ArrayList;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import musikverwaltung.CachedPathChooser;
import musikverwaltung.ScreenController;
import musikverwaltung.SettingFile;
import musikverwaltung.handler.ActionListenerManager;

public class SettingsView extends GenericView implements ActionListenerManager {
    final ObservableList<String> directories = FXCollections.observableArrayList();
    final CheckBox checkBox;

    public SettingsView(ScreenController sc) {
        super(sc, 350, 300);

        Button selectDirectory = new Button("Select Directory");
        selectDirectory.setOnAction(e -> {
            Path selectedDirectory = CachedPathChooser.showDialog(stage, null);
            if (selectedDirectory != null) {
                directories.add(selectedDirectory.toAbsolutePath().toString());
            }
        });
        ListView<String> listDirectory = new ListView<>(directories);
        listDirectory.setCellFactory(param -> new XCell(getWidthProperty()));
        listDirectory.setFocusTraversable(false);
        VBox.setVgrow(listDirectory, Priority.ALWAYS);

        checkBox = new CheckBox("Show unplayable songs");

        Button buttonSave = new Button("Save");
        buttonSave.setOnAction(e -> {
            SettingFile.savePaths(new ArrayList<>(directories));
            SettingFile.saveShowUnplayableSongs(checkBox.isSelected());
            stage.close();
            triggerActionListener();
        });
        Button buttonCancel = new Button("Cancel");
        buttonCancel.setCancelButton(true);
        buttonCancel.setOnAction(e -> stage.close());
        HBox buttonHBox = new HBox(buttonSave, buttonCancel);
        VBox settingsVBox = new VBox(selectDirectory, listDirectory, checkBox, buttonHBox);
        showNodes(settingsVBox);
    }

    @Override
    public Node get() {
        directories.setAll(SettingFile.load().getPaths());
        checkBox.setSelected(SettingFile.load().getShowUnplayableSongs());
        return super.get();
    }

    static class XCell extends ListCell<String> {
        final HBox hbox = new HBox();
        final Label label = new Label("");
        final Button button = new Button("(Del)");

        public XCell(DoubleBinding widthProperty) {
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
