package musikverwaltung.views;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import musikverwaltung.MediaManager;
import musikverwaltung.ScreenController;
import musikverwaltung.data.Playlist;

public class PlaylistDetailView extends MainView {
    private ObservableList<Playlist> contextPlaylists;
    private Playlist playlist;
    //TODO weg finden den hintergrund zu ändern
    public PlaylistDetailView(ScreenController sc, MediaManager mediaManager) {
        super(sc, mediaManager, true);
        ignoreMenuItems(mainViewButton, settingViewButton, playlistViewButton, creditsViewButton);

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(Control.USE_PREF_SIZE);
        deleteButton.setStyle("-fx-text-fill: #ef0505");
        deleteButton.setOnAction(e -> {
            getSelectedSongs().forEach(song -> {
                int i = song.getRowIndex();
                // TODO theoretisch könnte man hier auch playlist.get(i) == song machen, wenn der song überall
                //  nur referenziert wird
                if (i != -1 && i < playlist.size() && playlist.get(i).equals(song)) {
                    playlist.remove(i);
                } else {
                    // fallback
                    playlist.removeFirstOccurrence(song);
                }
                //TODO stage schließt sich jetzt am Ende -> bugs verhindern, player macht mit einzelsong korrekt weiter
                if (playlist.isEmpty()) {
                    contextPlaylists.remove(playlist);
                    stage.close();
                }
            });
            refresh().run();
        });

        showPlaylistAdd.addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                customButtonPane.getChildren().add(deleteButton);
            } else {
                customButtonPane.getChildren().remove(deleteButton);
            }
        });
    }

    public void showPlaylistInContext(Playlist playlist, ObservableList<Playlist> playlists) {
        this.playlist = playlist;
        this.contextPlaylists = playlists;
        songFilterForPlaylist.bind(Bindings.createObjectBinding(() -> playlist::contains));
        welcomeLabel.textProperty().bind(playlist.getNameProperty());
    }
}
