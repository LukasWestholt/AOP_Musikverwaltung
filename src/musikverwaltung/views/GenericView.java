package musikverwaltung.views;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import musikverwaltung.ActionListener;
import musikverwaltung.ScreenController;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericView {
    final StackPane stackPane = new StackPane();

    final ScreenController screenController;

    Stage stage;

    private final double prefWidth;
    private final double prefHeight;

    public static final double DEFAULT_WIDTH = 650;
    public static final double DEFAULT_HEIGHT = 560;

    private final List<ActionListener> listeners = new ArrayList<>();

    public GenericView(ScreenController sc, double prefWidth, double prefHeight) {
        screenController = sc;
        this.prefWidth = prefWidth;
        this.prefHeight = prefHeight;
    }

    public GenericView(ScreenController sc) {
        screenController = sc;
        this.prefWidth = DEFAULT_WIDTH;
        this.prefHeight = DEFAULT_HEIGHT;
    }

    public Node get() {
        return stackPane;
    }

    public void addActionListener(ActionListener toAdd) {
        if (toAdd != null) listeners.add(toAdd);
    }

    public GenericView clearActionListener() {
        listeners.clear();
        return this;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void bindSceneDimensions(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        stackPane.prefWidthProperty().bind(width);
        stackPane.prefHeightProperty().bind(height);
    }

    public DoubleBinding getWidthProperty() {
        return stackPane.prefWidthProperty().subtract(0);
    }

    @SuppressWarnings("unused")
    public DoubleBinding getHeightProperty() {
        return stackPane.prefHeightProperty().subtract(0);
    }

    public double getPrefHeight() {
        return prefHeight;
    }

    public double getPrefWidth() {
        return prefWidth;
    }

    public void showNodes(Node... nodes) {
        stackPane.getChildren().addAll(nodes);
    }


    void triggerActionListener() {
        for (ActionListener hl : listeners)
            hl.settingChangeListener();
    }
}
