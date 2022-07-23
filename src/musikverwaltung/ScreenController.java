package musikverwaltung;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;

// https://stackoverflow.com/a/37276108/8980073
public class ScreenController {
    private final HashMap<String, GenericView> screenMap = new HashMap<>();
    private final HashMap<String, Stage> stageMap = new HashMap<>();

    public ScreenController(Stage stage) {
        stageMap.put("main", stage);
    }

    public Stage getMain() {
        return stageMap.get("main");
    }
    public Scene getMainScene() {
        return getMain().getScene();
    }
    public javafx.collections.ObservableList<javafx.scene.Node> getMainChildren() {
        return ((Group) getMainScene().getRoot()).getChildren();
    }

    protected void addScreen(String name, GenericView view) {
        screenMap.put(name, view);
    }

    protected void activate(String name) {
        GenericView view = screenMap.get(name);
        Stage mainStage = getMain();
        view.bindSceneDimensions(getMainScene().widthProperty(), getMainScene().heightProperty());
        getMainChildren().clear();
        getMainChildren().add(view.get());
        activate(mainStage, view, name);
    }

    // https://genuinecoder.com/javafx-scene-switch-change-animation/
    protected void activate(String name, Boolean animated) {
        if (!animated) {
            activate(name);
            return;
        }

        GenericView view = screenMap.get(name);
        Stage mainStage = getMain();
        view.bindSceneDimensions(getMainScene().widthProperty(), getMainScene().heightProperty());
        Pane root = view.get();

        //Set Y of second scene to Height of window
        root.translateYProperty().set(getMainScene().getHeight());
        //Add second scene. Now both first and second scene is present
        getMainChildren().add(root);

        //Create new TimeLine animation
        Timeline timeline = new Timeline();
        //Animate Y property
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        //After completing animation, remove first scene
        timeline.setOnFinished(t -> getMainChildren().remove(0, getMainChildren().size() - 1));
        timeline.play();
        activate(mainStage, view, name);
    }

    protected void activate(String name, Boolean animated, Integer seconds) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        event -> activate(name, animated)
                )
        );
        timeline.play();
    }

    protected void activateWindow(String name, boolean neighborToMain, double width, double height) {
        Stage stage = stageMap.get(name);
        GenericView view = screenMap.get(name);
        if (stage != null) {
            activate(stage, view, name, neighborToMain);
            return;
        }
        stage = new Stage();
        stageMap.put(name, stage);
        Scene scene = new Scene(new Group(), width, (neighborToMain ? getMainScene().getHeight() : height));
        stage.setScene(scene);
        view.bindSceneDimensions(scene.widthProperty(), scene.heightProperty());
        ((Group) scene.getRoot()).getChildren().add(view.get());
        activate(stage, view, name, neighborToMain);
    }

    protected void activate(Stage stage, GenericView view, String name) {
        activate(stage, view, name, false);
    }
    protected void activate(Stage stage, GenericView view, String title, boolean neighborToMain) {
        if (neighborToMain) {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            Stage mainStage = getMain();
            double x = mainStage.getX() + mainStage.getWidth();
            double y = mainStage.getY();
            if (bounds.getMinX() + bounds.getWidth() < x + stage.getWidth() || bounds.getMinY() + bounds.getHeight() < y + stage.getHeight()) {
                System.out.println("out of bounds");
            } else {
                stage.setX(x);
                stage.setY(y);
            }
        }
        stage.setTitle(title);
        view.setStage(stage);
        stage.show();
        stage.toFront();
    }

    protected void addActionListener(String name, ActionListener listener) {
        GenericView view = screenMap.get(name);
        view.addListener(listener);
    }
}
