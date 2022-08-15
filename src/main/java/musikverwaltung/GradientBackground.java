package musikverwaltung;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class GradientBackground {

    final DoubleBinding width;
    final DoubleBinding height;

    public GradientBackground(DoubleBinding width, DoubleBinding height) {
        this.width = width;
        this.height = height;
    }

    public Rectangle getDefaultRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(width);
        rectangle.heightProperty().bind(height);
        rectangle.setFill(new LinearGradient(
                0, 0, 1, 1, true, //sizing
                CycleMethod.NO_CYCLE, //cycling
                new Stop(0, Color.web("#81c483")), //colors
                new Stop(1, Color.web("#fcc200")))
        );
        return rectangle;
    }

    public Rectangle getCustomRectangle(ArrayList<String> colours) {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(width);
        rectangle.heightProperty().bind(height);
        rectangle.setFill(new LinearGradient(
                0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(colours.get(0))),
                new Stop(1, Color.web(colours.get(1))))
        );
        return rectangle;
    }
}
