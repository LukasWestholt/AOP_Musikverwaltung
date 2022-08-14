package musikverwaltung.views;

import javafx.collections.ObservableList;
import musikverwaltung.data.Playlist;
import musikverwaltung.data.SettingFile;

public class SettingsSaveTask implements Runnable {

    final ObservableList<Playlist> playlists;

    SettingsSaveTask(ObservableList<Playlist> playlists) {
        System.out.println("thread initialized");
        this.playlists = playlists;
    }

    @Override
    public void run() {
        System.out.println("thread working, saving playlists");
        if (playlists != null) {
            SettingFile.saveMediaLibrary(playlists);
        }
    }
}
