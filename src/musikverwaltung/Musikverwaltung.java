package musikverwaltung;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Musikverwaltung extends Application {
    ScreenController screenController;

    public static void main(String[] args) {
        System.out.println("Programm Startpunkt");
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Musikverwaltung");
        Scene scene = new Scene(new Group(), 650, 450);
        stage.setScene(scene);
        stage.show();

        screenController = new ScreenController(stage);
        screenController.addScreen("hello", new HelloView(screenController));
        screenController.addScreen("musikverwaltung", new MainView(screenController));
        screenController.addScreen("songseite", new SongView(screenController));
        screenController.activate("hello");
        screenController.activate("musikverwaltung", true, 1);
    }
}
