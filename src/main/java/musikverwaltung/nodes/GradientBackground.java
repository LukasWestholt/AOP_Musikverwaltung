package musikverwaltung.nodes;

import java.util.List;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

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

    public Rectangle getCustomRectangle(List<String> colours) {
        if (colours.size() == 2) {
            return getCustom2ColourRectangle(colours);
        }
        if (colours.size() == 3) {
            return getCustom3ColourRectangle(colours);
        } else {
            return getDefaultRectangle();
        }
    }

    private Rectangle getCustom2ColourRectangle(List<String> colours) {
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

    private Rectangle getCustom3ColourRectangle(List<String> colours) {
        Rectangle rectangle = new Rectangle();
        rectangle.widthProperty().bind(width);
        rectangle.heightProperty().bind(height);
        rectangle.setFill(new LinearGradient(
                0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(colours.get(0))),
                new Stop(0.6, Color.web(colours.get(1))),
                new Stop(1, Color.web(colours.get(2))))
        );
        return rectangle;
    }
}
