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
import musikverwaltung.data.SettingFile;
import musikverwaltung.handler.ListenerInitiator;
import musikverwaltung.handler.RefreshListener;

public class SettingsView extends GenericView {
    private final ObservableList<String> directories = FXCollections.observableArrayList();
    private final CheckBox checkBox;

    public final ListenerInitiator<RefreshListener> listenerInitiator = new ListenerInitiator<>();

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
        listDirectory.setCellFactory(param -> new DirectoryCell(getWidthProperty()));
        listDirectory.setFocusTraversable(false);
        VBox.setVgrow(listDirectory, Priority.ALWAYS);

        checkBox = new CheckBox("Show unplayable songs");

        Button buttonSave = new Button("Save");
        buttonSave.setOnAction(e -> {
            SettingFile.savePaths(new ArrayList<>(directories));
            SettingFile.saveShowUnplayableSongs(checkBox.isSelected());
            stage.close();
            listenerInitiator.getListeners().forEach(l -> l.refresh().run());
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

    static class DirectoryCell extends ListCell<String> {
        final HBox hbox = new HBox();
        final Label label = new Label("");
        final Button button = new Button("(Del)");

        public DirectoryCell(DoubleBinding widthProperty) {
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
