package musikverwaltung;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.StackPane;

public class GenericView {

    final StackPane stackPane = new StackPane();

    final ScreenController screenController;

    public GenericView(ScreenController sc) {
        screenController = sc;
    }

    public StackPane get() {
        return stackPane;
    }

    public void bindSceneDimensions(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        stackPane.prefWidthProperty().bind(width);
        stackPane.prefHeightProperty().bind(height);
    }
}
