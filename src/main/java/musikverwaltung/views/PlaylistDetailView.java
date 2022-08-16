package musikverwaltung.views;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import musikverwaltung.MediaManager;
import musikverwaltung.ScreenController;
import musikverwaltung.data.Playlist;

public class PlaylistDetailView extends MainView {
    private Playlist playlist;

    private Runnable onPlaylistEmpty;

    public PlaylistDetailView(ScreenController sc, MediaManager mediaManager) {
        super(sc, mediaManager, true);
        ignoreMenuItems(mainViewButton, settingViewButton, playlistViewButton, creditsViewButton);

        for (Node node : stackPane.getChildren()) {
            if (node instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) node;
                rectangle.setFill(new LinearGradient(
                        0, 0, 1, 1, true,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#067457")),
                        new Stop(0.6, Color.web("#0EC47F")),
                        new Stop(1, Color.web("#5799D5"))) //#4DD393
                );
            }
        }

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(Control.USE_PREF_SIZE);
        //deleteButton.setStyle("-fx-text-fill: #ef0505");
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
                if (playlist.isEmpty()) {
                    if (onPlaylistEmpty != null) {
                        onPlaylistEmpty.run();
                    }
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

    public void showPlaylist(Playlist playlist) {
        this.playlist = playlist;
        songFilterForPlaylist.bind(Bindings.createObjectBinding(() -> playlist::contains, playlist.getAll()));
        welcomeLabel.textProperty().bind(playlist.getNameProperty());
    }

    public void onPlaylistEmpty(Runnable runnable) {
        this.onPlaylistEmpty = runnable;
    }
}
