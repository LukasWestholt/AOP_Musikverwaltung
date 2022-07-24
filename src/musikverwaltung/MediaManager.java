package musikverwaltung;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.media.Media;

import java.io.File;
import java.util.*;

public class MediaManager {
    final HashSet<File> mediaFiles = new HashSet<>();

    public final ObservableList<Musikstueck> music = FXCollections.observableArrayList(o -> new Observable[]{
            o.bekommeTitelProperty(),
            o.bekommeInterpretProperty(),
            o.bekommeGenreProperty()
    });

    final String[] types = {
            "wav", "mp3", "m4a", ".aif", ".aiff"
    };
    public void clearAndLoadAll(Runnable refreshCallback) {
        music.clear();
        mediaFiles.clear();

        List<String> list = SettingFile.load();
        for (final String folder_str : list) {
            listMediaFilesForFolder(new File(folder_str));
        }
        MapChangeListener<String, Object> metadataListener;
        for (final File mediaFile : mediaFiles) {
            music.add(new Musikstueck(mediaFile));
            Media media = new Media(mediaFile.toURI().toString());
            ObservableMap<String, Object> metadata_for_listener = media.getMetadata();
            metadataListener = metadata -> {
                //System.out.println(currentSong.getMetadata());
                FilteredList<Musikstueck> fl = music.filtered(p -> p.getPath() == mediaFile);
                if (fl.size() != 1) {
                    System.out.println("es gibt ein problem");
                    return;
                }
                Musikstueck musikstueck = fl.get(0);

                Object titel = metadata.getMap().get("title");
                if (titel != null && musikstueck.bekommeTitel().isEmpty()) {
                    musikstueck.setzeTitel(titel.toString());
                }
                Object interpret = metadata.getMap().get("artist");
                if (interpret != null && musikstueck.bekommeInterpret().isEmpty()) {
                    musikstueck.setzeInterpret(interpret.toString());
                }
                Object genre = metadata.getMap().get("genre");
                if (genre != null && musikstueck.bekommeGenre().isEmpty()) {
                    musikstueck.setzeGenre(genre.toString());
                }
                refreshCallback.run();
            };
            metadata_for_listener.addListener(metadataListener);
        }
    }

    public void listMediaFilesForFolder(File folder) {
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                listMediaFilesForFolder(fileEntry);
            } else {
                String extension;
                String fileName = fileEntry.getName();
                int i = fileName.lastIndexOf('.');
                int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

                if (i > p) {
                    extension = fileName.substring(i+1);
                    for (final String type : types) {
                        if (extension.equals(type)) {
                            mediaFiles.add(fileEntry);
                        }
                    }
                }
            }
        }
    }
}
