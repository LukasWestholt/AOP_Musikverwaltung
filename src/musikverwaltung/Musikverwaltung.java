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
        screenController.addScreen(SC.Hello, new HelloView(screenController));
        screenController.addScreen(SC.Musikverwaltung, new MainView(screenController));
        screenController.addScreen(SC.Player, new SongView(screenController));
        screenController.addScreen(SC.Einstellungen, new SettingsView(screenController));
        screenController.addScreen(SC.Playlist, new PlaylistView(screenController));
        screenController.activate(SC.Hello);
        screenController.activate(SC.Musikverwaltung, true, 1);
    }
}
