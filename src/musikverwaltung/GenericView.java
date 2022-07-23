package musikverwaltung;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GenericView {

    final StackPane stackPane = new StackPane();

    final ScreenController screenController;

    Stage stage;

    private final List<ActionListener> listeners = new ArrayList<>();

    public void addListener(ActionListener toAdd) {
        listeners.add(toAdd);
    }

    public GenericView(ScreenController sc) {
        screenController = sc;
    }

    public StackPane get() {
        return stackPane;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void bindSceneDimensions(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        stackPane.prefWidthProperty().bind(width);
        stackPane.prefHeightProperty().bind(height);
    }

    void triggerActionListener() {
        for (ActionListener hl : listeners)
            hl.settingChangeListener();
    }
}
