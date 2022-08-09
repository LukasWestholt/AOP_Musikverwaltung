package musikverwaltung;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import musikverwaltung.views.*;

public class Musikverwaltung extends Application {
    ScreenController screenController;
    final MediaManager mediaManager = new MediaManager();

    public static void main(String[] args) {
        System.out.println("Programm Startpunkt");
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mediaManager.lastSong = SettingFile.load().getLastSong();

        Scene scene = new Scene(new Group(), GenericView.DEFAULT_WIDTH, GenericView.DEFAULT_HEIGHT);
        stage.setScene(scene);
        stage.show();

        screenController = new ScreenController(stage);
        screenController.addScreen(new HelloView(screenController));
        screenController.addScreen(new MainView(screenController, mediaManager));
        screenController.addScreen(new SongView(screenController));
        screenController.addScreen(new SettingsView(screenController));
        screenController.addScreen(new PlaylistView(screenController, mediaManager));
        screenController.addScreen(new CreditsView(screenController));
        screenController.activate(HelloView.class);
        screenController.activate(MainView.class, true, 1);
    }

    @Override
    public void stop() {
        screenController.triggerDestroyListener();
    }
}
