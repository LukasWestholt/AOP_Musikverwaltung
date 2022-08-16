package musikverwaltung;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import musikverwaltung.data.*;

public class MediaManager {
    private final HashMap<Integer, String> genres = loadGenres();

    // this ObservableList will fire change events on add, delete etc AND on events of the extractor parameter
    // there is currently no listener on this object, so this feature is unused.
    public final ObservableList<Song> music = FXCollections.observableArrayList(o -> new Observable[]{
            o.getTitleProperty(),
            o.getArtistProperty(),
            o.getGenreProperty(),
            o.getCoverProperty()
    });

    private final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    private boolean showUnplayableSongs = false;

    private static final String genreFilename = "genres.txt";

    public void firstLoad() {
        update(null);

        // letzte Playlisten werden geladen. Dabei wird die Order eingehalten.
        for (PlaylistExternalizable playlistExt : SettingFile.load().getPlaylists()) {
            ArrayList<Song> songs = new ArrayList<>();

            for (Song song : music) {
                for (URIS uris : playlistExt.getPaths()) {
                    Path path = uris.getPath();
                    if (path == null) {
                        continue;
                    }
                    try {
                        if (Files.isSameFile(song.getPath(), path)) {
                            songs.add(song);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (URIS uris : playlistExt.getPaths()) {
                Path path = uris.getPath();
                if (path == null) {
                    continue;
                }
                boolean found = false;
                for (Song song : songs) {
                    try {
                        if (Files.isSameFile(song.getPath(), path)) {
                            found = true;
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (!found) {
                    Song song = new Song(path);
                    System.out.println("add unplayable: " + song);
                    song.setPlayable(false);
                    music.add(song);
                }
            }

            Playlist playlist = new Playlist(playlistExt.getName(), songs, playlistExt.getPreviewImagePath());
            playlists.add(playlist);
        }
    }

    public void update(Runnable refreshCallback) {
        HashSet<Path> mediaFiles = new HashSet<>();

        SettingFile settingFile = SettingFile.load();
        ArrayList<String> paths = settingFile.getPaths();
        for (String folder : paths) {
            try (Stream<Path> pathsStream = Files.walk(Helper.s2p(folder))) {
                pathsStream.filter(Files::isRegularFile).forEach(
                        path -> {
                            String pattern = "glob:" + Helper.audioExtensions;
                            PathMatcher matcher = FileSystems.getDefault().getPathMatcher(pattern);
                            if (matcher.matches(path.getFileName())) {
                                mediaFiles.add(path);
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        showUnplayableSongs = settingFile.getShowUnplayableSongs();

        for (Song song : music) {
            song.setPlayable(mediaFiles.contains(song.getPath()));
        }

        for (Path mediaFile : mediaFiles) {
            boolean found = false;
            for (Song song : music) {
                if (song.getPath().equals(mediaFile)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                music.add(new Song(mediaFile));
            }
        }

        for (Song song : getMusic(Whitelist.PLAYABLE)) {
            Media media = new URIS(song.getPath()).toMedia();
            media.getMetadata().addListener((MapChangeListener<String, Object>) metadata -> {
                FilteredList<Song> fl = music.filtered(
                        s -> s.isPlayable() && Objects.equals(s, song));
                if (fl.size() != 1) {
                    throw new InternalError("There is a problem");
                }
                Song song1 = fl.get(0);
                assert song1 == song;

                Object titel = metadata.getMap().get("title");
                if (titel != null && song1.getTitle().isEmpty()) {
                    song1.setTitle(titel.toString());
                }
                Object interpret = metadata.getMap().get("artist");
                if (interpret != null && song1.getArtist().isEmpty()) {
                    song1.setArtist(interpret.toString());
                }
                Object genre = metadata.getMap().get("genre");
                if (genre != null && song1.getGenre().isEmpty()) {
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
                    song1.setGenre(genreStr);
                }
                Object image = metadata.getMap().get("image");
                if (image != null && song1.getCover() == null && image instanceof Image) {
                    song1.setCover((Image) image);
                }
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            });
        }
    }

    /**
     * There is a list for genre ids:
     * <a href="https://en.wikipedia.org/wiki/List_of_ID3v1_Genres">Genre ids</a>.
     *
     */
    private HashMap<Integer, String> loadGenres() {
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

    public FilteredList<Song> getMusic(Whitelist whitelist) {
        switch (whitelist) {
            case PLAYABLE:
                return music.filtered(Song::isPlayable);
            case UNPLAYABLE:
                return music.filtered(s -> !s.isPlayable());
            case RESPECT:
                return music.filtered(s -> showUnplayableSongs || s.isPlayable());
            default:
                return new FilteredList<>(music);
        }
    }

    public ObservableList<Playlist> getPlaylists() {
        return this.playlists;
    }

    public enum Whitelist {
        PLAYABLE,
        UNPLAYABLE,
        RESPECT,

        ALL,
    }
}



