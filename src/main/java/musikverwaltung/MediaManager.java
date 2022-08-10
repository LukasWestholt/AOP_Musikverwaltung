package musikverwaltung;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.media.Media;

public class MediaManager {
    private final HashSet<Path> mediaFiles = new HashSet<>();
    private final HashMap<Integer, String> genres = loadGenres();

    public final ObservableList<Song> music = FXCollections.observableArrayList(o -> new Observable[]{
            o.getTitleProperty(),
            o.getArtistProperty(),
            o.getGenreProperty(),
            o.isSelectedProperty()
            // TODO is needed?
    });

    private static final String genreFilename = "genres.txt";

    public Path lastSong;

    public void clearAndLoadAll(Runnable refreshCallback) {
        music.clear();
        mediaFiles.clear();

        List<String> list = SettingFile.load().getPaths();
        for (final String folder : list) {
            try (Stream<Path> paths = Files.walk(Helper.s2p(folder))) {
                paths.filter(Files::isRegularFile).forEach(this::checkMediaExtension);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MapChangeListener<String, Object> metadataListener;
        for (final Path mediaFile : mediaFiles) {
            music.add(new Song(mediaFile));
            Media media = new Media(Helper.p2uris(mediaFile));
            ObservableMap<String, Object> metadataForListener = media.getMetadata();
            metadataListener = metadata -> {
                //System.out.println(currentSong.getMetadata());
                FilteredList<Song> fl = music.filtered(p -> p.getPath() == mediaFile);
                if (fl.size() != 1) {
                    System.out.println("es gibt ein problem");
                    return;
                }
                Song song = fl.get(0);

                Object titel = metadata.getMap().get("title");
                if (titel != null && song.getTitle().isEmpty()) {
                    song.setTitle(titel.toString());
                }
                Object interpret = metadata.getMap().get("artist");
                if (interpret != null && song.getArtist().isEmpty()) {
                    song.setArtist(interpret.toString());
                }
                Object genre = metadata.getMap().get("genre");
                if (genre != null && song.getGenre().isEmpty()) {
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
                    song.setGenre(genreStr);
                }
                refreshCallback.run();
            };
            metadataForListener.addListener(metadataListener);
        }
    }

    public void checkMediaExtension(Path path) {
        String pattern = "glob:*.{wav,mp3,m4a,aif,aiff}";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);
        if (matcher.matches(path.getFileName())) {
            mediaFiles.add(path);
        }
    }

    /**
     * There is a list for genre ids:
     * <a href="https://en.wikipedia.org/wiki/List_of_ID3v1_Genres">Genre ids</a>.
     *
     */
    public HashMap<Integer, String> loadGenres() {
        HashMap<Integer, String> genresMap = new HashMap<>();

        try (
                InputStream inputStream = this.getClass().getResourceAsStream("/" + genreFilename);
                InputStreamReader inputStreamReader = new InputStreamReader(Objects.requireNonNull(inputStream));
                BufferedReader br = new BufferedReader(inputStreamReader)
        ) {
            String line = br.readLine();
            while (line != null) {
                String[] array = line.split(" - ", 2);
                genresMap.put(Integer.valueOf(array[0]), array[1]);
                line = br.readLine();
            }
        } catch (IOException ignored) {
            return new HashMap<>();
        } catch (NullPointerException ignored) {
            System.out.println("Resource not found. Aborting.");
            System.exit(-1);
        }
        return genresMap;
    }

    public Song getLastSong() {
        if (lastSong != null) {
            for (final Song song : music) {
                try {
                    if (Files.isSameFile(song.getPath(), lastSong)) {
                        return song;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
