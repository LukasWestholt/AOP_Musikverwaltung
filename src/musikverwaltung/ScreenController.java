package musikverwaltung;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Objects;

// https://stackoverflow.com/a/37276108/8980073
public class ScreenController {
    private final HashMap<SC, GenericView> screenMap = new HashMap<>();
    private final HashMap<SC, Stage> stageMap = new HashMap<>();

    public ScreenController(Stage stage) {
        stageMap.put(SC.ROOT, stage);
    }

    public Stage getMain() {
        return stageMap.get(SC.ROOT);
    }
    public Scene getMainScene() {
        return getMain().getScene();
    }
    public javafx.collections.ObservableList<javafx.scene.Node> getMainChildren() {
        return ((Group) getMainScene().getRoot()).getChildren();
    }

    protected void addScreen(SC sc, GenericView view) {
        screenMap.put(sc, view);
    }

    protected GenericView activate(SC sc) {
        GenericView view = screenMap.get(sc);
        Stage mainStage = getMain();
        view.bindSceneDimensions(getMainScene().widthProperty(), getMainScene().heightProperty());
        getMainChildren().clear();
        getMainChildren().add(view.get());
        return activate(mainStage, view, sc.name());
    }

    // https://genuinecoder.com/javafx-scene-switch-change-animation/
    protected GenericView activate(SC sc, Boolean animated) {
        if (!animated) {
            return activate(sc);
        }

        GenericView view = screenMap.get(sc);
        Stage mainStage = getMain();
        view.bindSceneDimensions(getMainScene().widthProperty(), getMainScene().heightProperty());
        Node root = view.get();

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
        return activate(mainStage, view, sc.name());
    }

    protected void activate(SC sc, Boolean animated, Integer seconds) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        event -> activate(sc, animated)
                )
        );
        timeline.play();
    }

    protected GenericView activateWindow(SC sc, boolean neighborToMain, double width, double height) {
        Stage stage = stageMap.get(sc);
        GenericView view = screenMap.get(sc);
        if (stage != null) {
            return activate(stage, view, sc.name(), neighborToMain);
        }
        stage = new Stage();
        stageMap.put(sc, stage);
        Scene scene = new Scene(new Group(), width, (neighborToMain ? getMainScene().getHeight() : height));
        stage.setScene(scene);
        view.bindSceneDimensions(scene.widthProperty(), scene.heightProperty());
        ((Group) scene.getRoot()).getChildren().add(view.get());
        return activate(stage, view, sc.name(), neighborToMain);
    }

    protected GenericView activate(Stage stage, GenericView view, String title) {
        return activate(stage, view, title, false);
    }
    protected GenericView activate(Stage stage, GenericView view, String title, boolean neighborToMain) {
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
        stage.getScene().getStylesheets().add(Objects.requireNonNull(this.getClass().getResource("style.css")).toExternalForm());
        return view;
    }
}

enum SC {
    ROOT,
    Hello,
    Musikverwaltung,
    Player,
    Einstellungen,
    Playlist
}
