package musikverwaltung;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.media.Media;

public class MediaManager {
    private final HashSet<File> mediaFiles = new HashSet<>();
    private final HashMap<Integer, String> genres = loadGenres();

    public final ObservableList<Musikstueck> music = FXCollections.observableArrayList(o -> new Observable[]{
            o.bekommeTitelProperty(),
            o.bekommeInterpretProperty(),
            o.bekommeGenreProperty()
    });

    private final String[] types = {
            "wav", "mp3", "m4a", ".aif", ".aiff"
    };

    private static final String genreFilename = "genres.txt";

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
            ObservableMap<String, Object> metadataForListener = media.getMetadata();
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
                    String genreStr = genre.toString();

                    // https://regex101.com/r/NYHAf3/1
                    Pattern pattern = Pattern.compile("^\\((\\d+)\\)$");
                    Matcher matcher = pattern.matcher(genreStr);
                    if (matcher.matches()) {
                        int id = Integer.parseInt(matcher.group(1));
                        if (genres.containsKey(id)) {
                            genreStr = genres.get(id);
                        }
                    }
                    musikstueck.setzeGenre(genreStr);
                }
                refreshCallback.run();
            };
            metadataForListener.addListener(metadataListener);
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
                    extension = fileName.substring(i + 1);
                    for (final String type : types) {
                        if (extension.equals(type)) {
                            mediaFiles.add(fileEntry);
                        }
                    }
                }
            }
        }
    }

    /**
     * There is a list for genre ids:
     * <a href="https://en.wikipedia.org/wiki/List_of_ID3v1_Genres">Genre ids</a>.
     *
     */
    public static HashMap<Integer, String> loadGenres() {
        HashMap<Integer, String> genresMap = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(genreFilename));
            String line = br.readLine();
            while (line != null) {
                String[] array = line.split(" - ", 2);
                genresMap.put(Integer.valueOf(array[0]), array[1]);
                line = br.readLine();
            }
            br.close();
        } catch (IOException ignored) {
            return new HashMap<>();
        }
        return genresMap;
    }
}
