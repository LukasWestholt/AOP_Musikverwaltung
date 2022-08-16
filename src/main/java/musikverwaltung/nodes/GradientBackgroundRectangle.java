package musikverwaltung.nodes;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class GradientBackgroundRectangle extends Rectangle{

    public GradientBackgroundRectangle(DoubleBinding width, DoubleBinding height) {
        super();
        widthProperty().bind(width);
        heightProperty().bind(height);
        setFill(new LinearGradient(
                0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#81c483")),
                new Stop(1, Color.web("#fcc200")))
        );
    }

    public GradientBackgroundRectangle(DoubleBinding width, DoubleBinding height, List<String> colours) {
        super();
        int len = colours.size();
        Stop[] listOfStops = new Stop[len];
        for (int i = 0; i < len; i++) {
            listOfStops[i] = new Stop(1.0/len * i, Color.web(colours.get(i)));
        }
        widthProperty().bind(width);
        heightProperty().bind(height);
        setFill(new LinearGradient(
                0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                listOfStops
        ));
    }
}
