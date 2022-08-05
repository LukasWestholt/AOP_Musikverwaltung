package musikverwaltung;

import java.net.URL;
import java.util.HashMap;
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
import musikverwaltung.views.GenericView;

// https://stackoverflow.com/a/37276108/8980073
public class ScreenController {
    private final HashMap<Class<? extends GenericView>, GenericView> screenMap = new HashMap<>();
    private final HashMap<Class<? extends GenericView>, Stage> stageMap = new HashMap<>();

    public ScreenController(Stage stage) {
        stageMap.put(GenericView.class, stage);
    }

    public Stage getMain() {
        return stageMap.get(GenericView.class);
    }

    public Scene getMainScene() {
        return getMain().getScene();
    }

    public javafx.collections.ObservableList<javafx.scene.Node> getMainChildren() {
        return ((Group) getMainScene().getRoot()).getChildren();
    }

    protected void addScreen(GenericView view) {
        screenMap.put(view.getClass(), view);
    }

    public GenericView activate(Class<? extends GenericView> id) {
        GenericView view = screenMap.get(id);
        view.bindSceneDimensions(getMainScene().widthProperty(), getMainScene().heightProperty());
        getMainChildren().clear();
        getMainChildren().add(view.get());
        return activate(getMain(), view, id.getName()); // TODO (LW) class.getName() or better: String in GenericView
    }

    // https://genuinecoder.com/javafx-scene-switch-change-animation/
    public GenericView activate(Class<? extends GenericView> id, Boolean animated) {
        if (!animated) {
            return activate(id);
        }

        GenericView view = screenMap.get(id);
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
        return activate(getMain(), view, id.getName());
    }

    public void activate(Class<? extends GenericView> id, Boolean animated, Integer seconds) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(seconds),
                        event -> activate(id, animated)
                )
        );
        timeline.play();
    }

    public GenericView activate(Stage stage, GenericView view, String title) {
        return activate(stage, view, title, false);
    }

    public GenericView activate(Stage stage, GenericView view, String title, boolean neighborToMain) {
        if (neighborToMain) {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            Stage mainStage = getMain();
            double x = mainStage.getX() + mainStage.getWidth();
            double y = mainStage.getY();
            if (bounds.getMinX() + bounds.getWidth() < x + stage.getWidth()
                    || bounds.getMinY() + bounds.getHeight() < y + stage.getHeight()) {
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

        URL url = this.getClass().getResource("/style.css");
        if (url == null) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        stage.getScene().getStylesheets().add(url.toExternalForm());
        return view;
    }

    public GenericView activateWindow(Class<? extends GenericView> id, boolean neighborToMain) {
        Stage stage = stageMap.get(id);
        GenericView view = screenMap.get(id);
        if (stage != null) {
            return activate(stage, view, id.getName(), neighborToMain);
        }
        stage = new Stage();
        stageMap.put(id, stage);
        Scene scene = new Scene(new Group(), view.getPrefWidth(),
                (neighborToMain ? getMainScene().getHeight() : view.getPrefHeight()));
        stage.setScene(scene);
        view.bindSceneDimensions(scene.widthProperty(), scene.heightProperty());
        ((Group) scene.getRoot()).getChildren().add(view.get());
        return activate(stage, view, id.getName(), neighborToMain);
    }

    public void triggerDestroyListener() {
        for (GenericView view : screenMap.values()) {
            view.triggerDestroyListener();
        }
    }
}
