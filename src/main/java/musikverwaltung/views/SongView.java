package musikverwaltung.views;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javafx.beans.binding.When;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import musikverwaltung.*;
import musikverwaltung.data.Playlist;
import musikverwaltung.data.SettingFile;
import musikverwaltung.data.Song;
import musikverwaltung.handler.DestroyListener;
import musikverwaltung.handler.ListenerInitiator;
import musikverwaltung.handler.SetActionLabelListener;

//TODO einzelsong bei repeat hin und her wechseln sehr verbuggt
public class SongView extends MenuBarView implements DestroyListener {
    private final MediaManager mediaManager;
    private double songLength = Double.MAX_VALUE;
    private double volume = 0.5;
    private final SimpleBooleanProperty chartIsVisible = new SimpleBooleanProperty();
    private final ImageButton startStop;
    private final Image playImage;
    private final Image pauseImage;
    private final Label labelSongName;
    final ImageView imageView = new ImageView();
    private final Image defaultImage = new Image(
            Helper.getResourcePathUriString(this.getClass(), "/default_img.jpg", false)
    );
    private MediaPlayer player;
    private final int dbThreshold = 60;
    private XYChart.Series<String, Number> audioData;
    private final AudioSpectrumListener audioSpectrumListener;
    private final ChangeListener<Duration> playerSongLengthListener;
    private Playlist playlist;
    private final SongHistoryList songHistoryStack = new SongHistoryList(10);
    private boolean onRepeat = true;
    public final ListenerInitiator<SetActionLabelListener> listenerInitiator = new ListenerInitiator<>();

    public SongView(ScreenController sc, MediaManager mediaManager) {
        /*
        https://www.geeksforgeeks.org/javafx-progressbar/
        https://stackoverflow.com/questions/26850828/how-to-make-a-javafx-button-with-circle-shape-of-3xp-diameter
        http://kenyadevelopers.blogspot.com/2015/06/javafx-audiospectrum-and-barchartbeauty.html
         */
        super(sc, 320, 560);

        this.mediaManager = mediaManager;

        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(MainView.class)
        );
        Button onRepeatButton = new Button("Repeat");
        addActiveMenuButton(onRepeatButton,
                e -> {
                    onRepeat = !onRepeat;
                    onRepeatButton.setText(onRepeat ? "Repeat" : "No Repeat");
                }
        );
        ignoreMenuItems(settingViewButton, playlistViewButton, creditsViewButton);

        screenController.listenerInitiator.addListenerIfNotContains(this);

        labelSongName = new Label("Unbekannt");
        labelSongName.getStyleClass().add("songViewHeader");
        labelSongName.setStyle("-fx-font-size: 25pt; -fx-font-family: Manrope-Light;"
                + "-fx-text-fill: rgb(225, 228, 203);");

        StackPane centerContainer = new StackPane();
        centerContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerContainer, Priority.ALWAYS);

        MenuItem headerMenu = new MenuItem("Ansicht wechseln:");
        headerMenu.getStyleClass().add("header-menu-item");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem radioImageMenu = new RadioMenuItem("Bild");
        radioImageMenu.setSelected(true);
        radioImageMenu.setToggleGroup(toggleGroup);
        RadioMenuItem radioChartMenu = new RadioMenuItem("Graph");
        radioChartMenu.setToggleGroup(toggleGroup);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(headerMenu, radioImageMenu, radioChartMenu);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.fitWidthProperty().bind(getWidthProperty().subtract(30));
        imageView.fitHeightProperty().bind(getHeightProperty().divide(2));
        imageView.setOnMouseClicked(event -> contextMenu.show(imageView, event.getScreenX(), event.getScreenY()));
        imageView.setImage(defaultImage);
        centerContainer.getChildren().add(imageView);

        Slider songSlider = new Slider(0, 1, 0);
        songSlider.getStyleClass().add("song");
        songSlider.prefHeightProperty().bind(getHeightProperty().divide(20));
        ProgressBar songProgressBar = new ProgressBar(0);
        songProgressBar.getStyleClass().add("song");
        songProgressBar.prefHeightProperty().bind(getHeightProperty().divide(20));
        songProgressBar.prefWidthProperty().bind(songSlider.widthProperty());
        playerSongLengthListener = (o, oldPosition, newPosition) -> {
            songProgressBar.setProgress(newPosition.toSeconds() / songLength);
            if (!songSlider.isValueChanging()) {
                songSlider.setValue(newPosition.toSeconds() / songLength);
            }
        };
        songSlider.valueChangingProperty().addListener((observableValue, wasChanging, changing) -> {
            if (!changing) {
                System.out.println("slider change finished");
                if (!isPlayerUnavailable()) {
                    player.seek(Duration.seconds(songLength * songSlider.getValue()));
                }
            }
        });
        //TODO image ist nicht mittig!!!!!!
        playImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/icons/play.png", false));
        pauseImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/icons/pause.png", false));
        startStop = new ImageButton(playImage, true, true);
        startStop.setOnAction(e -> startStopSong());
        startStop.setPrefSize(30, 30);
        startStop.setMaxWidth(Double.MAX_VALUE);
        startStop.maxHeightProperty().bind(startStop.widthProperty());

        HBox.setHgrow(startStop, Priority.SOMETIMES);

        ImageButton skipForward = new ImageButton(
                Helper.getResourcePath(this.getClass(), "/icons/skip.png", false),
                false, true
        );
        skipForward.setOnAction(e -> skipforwards());
        setDynamicSize(skipForward);
        skipForward.setPrefSize(30, 30);

        ImageButton skipBackward = new ImageButton(
                Helper.getResourcePath(this.getClass(), "/icons/skipback.png", false),
                false, true
        );
        skipBackward.setOnAction(e -> skipbackwards());
        setDynamicSize(skipBackward);
        skipBackward.setPrefSize(30, 30);

        /*startStop.minWidthProperty().bind(
                Bindings.max(skipBackward.heightProperty(), skipForward.heightProperty())
        );*/
        ImageButton skipAhead = new ImageButton(
                Helper.getResourcePath(this.getClass(), "/icons/15sAhead.png", false),
                false, true
                );
        skipAhead.setOnAction(e -> skipTime(15));
        setDynamicSize(skipAhead);
        skipAhead.setPrefSize(30, 30);

        ImageButton skipBehind = new ImageButton(Helper.getResourcePath(
                this.getClass(), "/icons/15sBack.png", false),
                false, true);
        skipBehind.setOnAction(e -> skipTime(-15));
        setDynamicSize(skipBehind);
        skipBehind.setPrefSize(30, 30);

        HBox buttonHBox = new HBox(skipBehind, skipBackward, startStop, skipForward, skipAhead);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.setSpacing(10);
        buttonHBox.maxWidthProperty().bind(getHeightProperty().divide(2));

        Slider slider = new Slider(0, 1, 0);
        slider.setValue(volume);
        slider.getStyleClass().add("volume");
        slider.valueProperty().addListener((useless1, useless2, sliderValue) -> {
            volume = sliderValue.doubleValue();
            if (!isPlayerUnavailable()) {
                player.setVolume(volume);
            }
        });

        HBox sliderHBox = new HBox(slider);
        sliderHBox.maxWidthProperty().bind(new When(startStop.widthProperty().lessThan(120))
                .then(120).otherwise(startStop.widthProperty()));
        sliderHBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(slider, Priority.ALWAYS);

        VBox.setVgrow(buttonHBox, Priority.ALWAYS);
        StackPane songSliderProgressbar = new StackPane(songProgressBar, songSlider);
        VBox mediaControlVBox = new VBox(songSliderProgressbar, buttonHBox, sliderHBox);
        mediaControlVBox.setAlignment(Pos.CENTER);
        mediaControlVBox.setSpacing(10);
        mediaControlVBox.setStyle("-fx-background-color: beige;");
        VBox.setMargin(mediaControlVBox, new Insets(0, 15, 15, 15));

        audioSpectrumListener = (timestamp, duration, magnitudes, phases) -> {
            for (int i = 0; i < magnitudes.length; i++) {
                System.out.println("doing");
                audioData.getData().add(new XYChart.Data<>(Integer.toString(i), magnitudes[i] + dbThreshold));
            }
        };
        CategoryAxis horizontalAxis = new CategoryAxis();
        NumberAxis verticalAxis = new NumberAxis();
        BarChart<String, Number> audioBarChart = new BarChart<>(horizontalAxis, verticalAxis);
        audioBarChart.setBarGap(0);
        audioBarChart.setCategoryGap(0);
        audioBarChart.setLegendVisible(false);
        audioBarChart.setAnimated(false);
        audioBarChart.setVerticalGridLinesVisible(false);
        audioBarChart.setHorizontalGridLinesVisible(false);
        audioBarChart.setHorizontalZeroLineVisible(false);
        audioBarChart.setVerticalZeroLineVisible(false);
        horizontalAxis.setTickMarkVisible(false);
        horizontalAxis.setTickLabelsVisible(false);
        verticalAxis.setTickMarkVisible(false);
        verticalAxis.setTickLabelsVisible(false);
        audioBarChart.prefWidthProperty().bind(getWidthProperty().subtract(30));
        audioBarChart.prefHeightProperty().bind(getHeightProperty().divide(2));
        audioData = new XYChart.Series<>();
        audioData.setName("audioData");
        audioBarChart.getData().add(audioData);
        audioBarChart.setOnMouseClicked(event ->
                contextMenu.show(audioBarChart, event.getScreenX(), event.getScreenY())
        );

        chartIsVisible.addListener((observableValue, oldVal, newVal) -> {
            if (newVal) {
                if ((!isPlayerUnavailable())) {
                    player.setAudioSpectrumListener(audioSpectrumListener);
                }
            } else {
                if ((!isPlayerUnavailable())) {
                    player.setAudioSpectrumListener(null);
                }
            }
        });

        toggleGroup.selectedToggleProperty().addListener((observableValue, oldVal, newVal) -> {
            RadioMenuItem selectedMenu = (RadioMenuItem) toggleGroup.getSelectedToggle();
            switch (selectedMenu.getText()) {
                case "Bild":
                    centerContainer.getChildren().clear();
                    centerContainer.getChildren().add(imageView);
                    chartIsVisible.set(false);
                    break;
                case "Graph":
                    centerContainer.getChildren().clear();
                    centerContainer.getChildren().add(audioBarChart);
                    chartIsVisible.set(true);
                    break;
                default:
                    centerContainer.getChildren().clear();
            }
        });

        GradientBackground gradientMaker = new GradientBackground(getWidthProperty(), getHeightProperty());
        List<String> colours = Arrays.asList("#222A35", "#203864", "#4472C4");
        Rectangle background = gradientMaker.getCustomRectangle(colours);



        VBox playerVBox = new VBox(labelSongName, centerContainer, mediaControlVBox);
        playerVBox.setAlignment(Pos.CENTER);
        playerVBox.setSpacing(10);
        showNodes(background, playerVBox);

    }

    @Override
    public Node get() {
        // on exit the automatic graph updates will stop
        // if stage shown and graph was last activated, it gets activated again
        stage.showingProperty().addListener((observableValue, oldVal, isShowing) -> {
            if (chartIsVisible.get() && isShowing) {
                player.setAudioSpectrumListener(audioSpectrumListener);
            } else {
                player.setAudioSpectrumListener(null);
            }
        });
        return super.get();
    }

    private void startStopSong() {
        if (isPlayerUnavailable()) {
            return;
        }
        if (isPlayerPlaying()) {
            player.pause();
            startStop.switchImage(playImage);
            listenerInitiator.getListeners().forEach(l -> l.setActionLabel("Stoppe Musik"));
        } else {
            player.play();
            startStop.switchImage(pauseImage);
            listenerInitiator.getListeners().forEach(l -> l.setActionLabel("Spiele: " + labelSongName.getText()));
        }
    }

    private void reset() {
        if (isPlayerUnavailable()) {
            return;
        }
        player.dispose();
        startStop.switchImage(playImage);
        audioData.getData().clear();
    }

    private void updateSong(Song nextSong, boolean startPlaying) {
        reset();
        if (nextSong == null) {
            return;
        }
        songHistoryStack.add(nextSong);
        Path path = nextSong.getPath();
        labelSongName.setText(nextSong.getTitle());
        Image cover = nextSong.getCover();
        if (cover != null && !cover.isError()) {
            imageView.setImage(cover);
        } else {
            imageView.setImage(defaultImage);
        }
        Media currentSong = new Media(Helper.p2uris(path));
        // TODO memory leak on Media/MediaPlayer ? i cant delete music files after they got played
        assert player == null || player.getStatus() == MediaPlayer.Status.DISPOSED;
        player = new MediaPlayer(currentSong);
        player.setOnEndOfMedia(this::skipforwards);
        player.setVolume(volume);

        //next song starts immediately or stops before
        if (startPlaying) {
            startStopSong();
        }
        currentSong.durationProperty().addListener((arg0, arg1, duration) -> songLength = duration.toSeconds());
        player.currentTimeProperty().addListener(playerSongLengthListener);
        if (chartIsVisible.get()) {
            player.setAudioSpectrumListener(audioSpectrumListener);
            player.setAudioSpectrumThreshold(-dbThreshold);
        }
        listenerInitiator.getListeners().forEach(l -> l.setActionLabel("Spiele: " + labelSongName.getText()));
    }

    private void skipforwards() {
        if (playlist == null) {
            return;
        }
        updateSong(playlist.getRelativeSong(1, onRepeat), true);
    }

    private void skipbackwards() {
        if (playlist == null) {
            return;
        }
        if (!isPlayerUnavailable() && player.getCurrentTime().toSeconds() < 2 && songLength > 10) {
            // skip backwards to the song before
            updateSong(playlist.getRelativeSong(-1, onRepeat), true);
        } else {
            // skip backwards to the beginning of the song
            updateSong(playlist.getRelativeSong(0, onRepeat), true);
        }
    }

    private void skipTime(int timeInSeconds) {
        if (isPlayerUnavailable()) {
            return;
        }
        player.seek(new Duration(player.getCurrentTime().toMillis() + (timeInSeconds * 1000)));
    }

    private boolean isPlayerPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    private boolean isPlayerUnavailable() {
        return player == null
                || player.getStatus() == MediaPlayer.Status.DISPOSED
                || player.getStatus() == MediaPlayer.Status.HALTED;
    }

    void setPlaylist(Playlist playlist, boolean startPlaying) {
        this.playlist = playlist;
        this.playlist.resetRemainingSongs();
        updateSong(playlist.getRelativeSong(1, onRepeat), startPlaying);
    }

    void setPlaylist(Song song, boolean startPlaying) {
        if (song == null || !song.isPlayable()) {
            return;
        }
        Playlist singleSongPlaylist = new Playlist();
        singleSongPlaylist.add(song);
        setPlaylist(singleSongPlaylist, startPlaying);
    }

    void setPlaylistLastSong() {
        if (!isPlayerPlaying()) {
            setPlaylist(getLastSong(), false);
        }
    }

    private static void setDynamicSize(Region region) {
        region.setMinWidth(Control.USE_PREF_SIZE);
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMinHeight(Control.USE_PREF_SIZE);
        region.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(region, Priority.SOMETIMES);
    }

    public Song getLastSong() {
        Song lastPlayedSong = songHistoryStack.peekLast();
        if (lastPlayedSong != null) {
            return lastPlayedSong;
        }
        Path lastPlayedSongPath = SettingFile.load().getLastSong();
        if (lastPlayedSongPath != null) {
            for (Song song : mediaManager.getPlayableMusic()) {
                try {
                    if (Files.isSameFile(song.getPath(), lastPlayedSongPath)) {
                        return song;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        Song lastSong = getLastSong();
        if (lastSong != null) {
            SettingFile.saveLastSong(getLastSong().getPath());
        } else {
            SettingFile.saveLastSong(null);
        }
    }
}
