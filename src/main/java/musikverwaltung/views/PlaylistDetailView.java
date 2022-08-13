package musikverwaltung.views;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import musikverwaltung.MediaManager;
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;

public class PlaylistDetailView extends MainView {
    //TODO playlist erstellen menu öffnet sich auch in diesem view

    ObservableList<Playlist> contextPlaylists;
    Playlist playlist;

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
                //  nur refrenziert wird
                if (i != -1 && i < playlist.size() && playlist.get(i).equals(song)) {
                    playlist.remove(i);
                } else {
                    //System.out.println("Use fallback removeFirstOccurrence");
                    playlist.removeFirstOccurrence(song);
                }
                if (playlist.isEmpty())
                    contextPlaylists.remove(playlist);
                //TODO maybe beenden von window wenn alle gelöscht sind 
            });
            refresh().run();
        });
        customButtonPane.getChildren().add(deleteButton);
    }

    public void showPlaylistInContext(Playlist playlist, ObservableList<Playlist> playlists) {
        this.playlist = playlist;
        this.contextPlaylists = playlists;
        songFilterForPlaylist.bind(Bindings.createObjectBinding(() -> playlist::contains));
        welcomeLabel.textProperty().bind(playlist.getNameProperty());
    }
}
