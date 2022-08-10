package musikverwaltung.views;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import musikverwaltung.MediaManager;
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;

public class PlaylistDetailView extends MainView {

    Playlist playlist;
    public PlaylistDetailView(ScreenController sc, MediaManager mediaManager) {
        super(sc, mediaManager);
        ignoreMenuItems(mainViewButton, settingViewButton, playlistViewButton, creditsViewButton);

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(Control.USE_PREF_SIZE);
        deleteButton.setStyle("-fx-text-fill: #ef0505");
        deleteButton.setOnAction(e -> {
            getSelectedSongs().forEach(song -> playlist.remove(song));
            refresh().run();
        });
        customButtonPane.getChildren().add(deleteButton);
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
        songFilterForPlaylist.bind(Bindings.createObjectBinding(() -> playlist::contains));
        welcomeLabel.textProperty().bind(playlist.getNameProperty());
    }
}
