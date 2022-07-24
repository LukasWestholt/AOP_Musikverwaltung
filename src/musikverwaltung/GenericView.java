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

    public void addActionListener(ActionListener toAdd) {
        if (toAdd != null) listeners.add(toAdd);
    }

    public GenericView clearActionListener() {
        listeners.clear();
        return this;
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
