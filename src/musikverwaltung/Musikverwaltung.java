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
    public void start(Stage stage) {
        Scene scene = new Scene(new Group(), 650, 450);
        stage.setScene(scene);
        stage.show();

        screenController = new ScreenController(stage);
        screenController.addScreen("Hello", new HelloView(screenController));
        screenController.addScreen("Musikverwaltung", new MainView(screenController));
        screenController.addScreen("Player", new SongView(screenController));
        screenController.addScreen("Einstellungen", new SettingsView(screenController));
        screenController.activate("Hello");
        screenController.activate("Musikverwaltung", true, 1);
    }
}
