package musikverwaltung;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.layout.StackPane;

public class GenericView {

    StackPane stackPane = new StackPane();
    ReadOnlyDoubleProperty scene_width;
    ReadOnlyDoubleProperty scene_height;

    public GenericView(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        scene_width = width;
        scene_height = height;
        stackPane.prefWidthProperty().bind(width);
        stackPane.prefHeightProperty().bind(height);
    }
}
