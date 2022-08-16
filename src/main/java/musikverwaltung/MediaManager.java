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
import musikverwaltung.data.Playlist;
import musikverwaltung.data.PlaylistExternalizable;
import musikverwaltung.data.SettingFile;
import musikverwaltung.data.Song;

public class MediaManager {
    private final HashSet<Path> mediaFiles = new HashSet<>();
    private final HashMap<Integer, String> genres = loadGenres();

    // TODO was macht das?
    public final ObservableList<Song> music = FXCollections.observableArrayList(o -> new Observable[]{
            o.getTitleProperty(),
            o.getArtistProperty(),
            o.getGenreProperty()
    });

    public final ObservableList<Playlist> playlists = FXCollections.observableArrayList();

    // TODO einlesen bei Jazzy night und ambient pearls hat Probleme
    private static final String genreFilename = "genres.txt";

    public MediaManager() {
        /*playlists.addListener((ListChangeListener<Playlist>) c -> {
            ArrayList<Playlist> changedPlaylists = new ArrayList<>();
            while (c.next()) {
                changedPlaylists.addAll(c.getAddedSubList());
                if (c.wasPermutated() || c.wasUpdated()) {
                    throw new UnsupportedOperationException();
                }
            }
            for (Playlist playlist : changedPlaylists) {
                playlist.getAll().addListener((ListChangeListener<Song>) change -> {
                    if (playlist.isEmpty()) {
                        System.out.println("isEmpty");
                        playlists.remove(playlist);
                    }
                });
            }
        });*/
        clearAndLoadAll(() -> {});

        // letzte Playlisten werden geladen
        SettingFile settingFile = SettingFile.load();
        for (PlaylistExternalizable playlistExt : settingFile.getPlaylists()) {
            ArrayList<Song> songs = new ArrayList<>();
            for (String string : playlistExt.getPaths()) {
                Path path = Helper.uris2p(string);
                if (path == null) {
                    continue;
                }
                boolean found = false;
                for (Song song : music) {
                    try {
                        if (Files.isSameFile(song.getPath(), path)) {
                            songs.add(song);
                            found = true;
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // TODO settingFile.getShowUnplayableSongs() is wrong here
                if (!found && settingFile.getShowUnplayableSongs()) {
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

    public void clearAndLoadAll(Runnable refreshCallback) {
        music.clear();
        mediaFiles.clear();

        ArrayList<String> paths = SettingFile.load().getPaths();
        for (String folder : paths) {
            try (Stream<Path> pathsStream = Files.walk(Helper.s2p(folder))) {
                pathsStream.filter(Files::isRegularFile).forEach(this::checkMediaExtension);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        MapChangeListener<String, Object> metadataListener;
        for (Path mediaFile : mediaFiles) {
            music.add(new Song(mediaFile));
            Media media = new Media(Helper.p2uris(mediaFile));
            metadataListener = metadata -> {
                // TODO there are some more metadata like albums, year
                //System.out.println(metadata.getMap());
                FilteredList<Song> fl = music.filtered(
                        p -> p.isPlayable() && Objects.equals(p.getPath(), mediaFile));
                if (fl.size() != 1) {
                    throw new InternalError("There is a problem");
                }
                Song song = fl.get(0);

                // TODO big error some music dont get
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
                Object image = metadata.getMap().get("image");
                if (image != null && song.getCover() == null && image instanceof Image) {
                    song.setCover((Image) image);
                }
                refreshCallback.run();
            };
            media.getMetadata().addListener(metadataListener);
        }
        // TODO save playlists earlier
    }

    private void checkMediaExtension(Path path) {
        String pattern = "glob:" + Helper.audioExtensions;
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

    public FilteredList<Song> getPlayableMusic() {
        return music.filtered(Song::isPlayable);
    }
}



