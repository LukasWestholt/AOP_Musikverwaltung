package musikverwaltung;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.HashMap;

// https://stackoverflow.com/a/37276108/8980073
public class ScreenController {
    private final HashMap<String, Pane> screenMap = new HashMap<>();
    private final Scene main;

    public ScreenController(Scene main) {
        this.main = main;
    }

    protected void addScreen(String name, Pane pane) {
        screenMap.put(name, pane);
    }

    protected void removeScreen(String name) {
        screenMap.remove(name);
    }

    protected void activate(String name) {
        ((Group) main.getRoot()).getChildren().clear();
        ((Group) main.getRoot()).getChildren().add(screenMap.get(name));
    }

    // https://genuinecoder.com/javafx-scene-switch-change-animation/
    protected void activate(String name, Boolean animated) {
        if (!animated) {
            activate(name);
            return;
        }
        Pane root = screenMap.get(name);

        //Set Y of second scene to Height of window
        root.translateYProperty().set(main.getHeight());
        //Add second scene. Now both first and second scene is present
        ((Group) main.getRoot()).getChildren().add(root);

        //Create new TimeLine animation
        Timeline timeline = new Timeline();
        //Animate Y property
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        //After completing animation, remove first scene
        timeline.setOnFinished(t -> ((Group) main.getRoot()).getChildren().remove(0, ((Group) main.getRoot()).getChildren().size() - 1));
        timeline.play();
    }

    protected void activate(String name, Boolean animated, Integer seconds) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        event -> activate(name, animated)
                )
        );
        timeline.play();
    }
}
