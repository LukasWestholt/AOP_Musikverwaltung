package musikverwaltung.views;

import java.nio.file.Path;

import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
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
import javafx.util.Duration;
import musikverwaltung.*;
import musikverwaltung.handler.StringListenerManager;

public class SongView extends MenuBarView implements StringListenerManager {
    double songLength;
    double volume = 0.5;
    boolean chartIsVisible;
    final Image defaultImage;
    final ImageView img;
    final ImageButton startStop;
    final Image playImage;
    final Image pauseImage;
    final Label labelSongName;
    Media currentSong;
    MediaPlayer player;
    final int dBThreshold = 60;
    XYChart.Series<String, Number> audioData;
    final AudioSpectrumListener audioSpectrumListener;
    Playlist playlist;
    private final SongHistoryList songHistoryStack = new SongHistoryList(10);
    private final ChangeListener<Duration> playerSongLengthListener;

    private static final boolean onRepeat = true;

    public SongView(ScreenController sc) {
        /*
        https://www.geeksforgeeks.org/javafx-progressbar/
        https://stackoverflow.com/questions/26850828/how-to-make-a-javafx-button-with-circle-shape-of-3xp-diameter
        http://kenyadevelopers.blogspot.com/2015/06/javafx-audiospectrum-and-barchartbeauty.html
         */
        super(sc, 320, 560);
        defaultImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/default_img.jpg", false));

        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(MainView.class)
        );
        addActiveMenuButton(new Button("Reset"),
                e -> reset(false)
        );
        ignoreMenuItems(settingViewButton, playlistViewButton, creditsViewButton);

        labelSongName = new Label("Unbekannt");
        labelSongName.getStyleClass().add("header");

        StackPane centerContainer = new StackPane();
        centerContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerContainer, Priority.ALWAYS);

        final ContextMenu switchCenterObject = new ContextMenu();
        final MenuItem headerMenu = new MenuItem("Ansicht wechseln:");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem radioImageMenu = new RadioMenuItem("Bild");
        radioImageMenu.setSelected(true);
        radioImageMenu.setToggleGroup(toggleGroup);
        RadioMenuItem radioChartMenu = new RadioMenuItem("Graph");
        radioChartMenu.setToggleGroup(toggleGroup);
        switchCenterObject.getItems().addAll(headerMenu, radioImageMenu, radioChartMenu);
        img = new ImageView();
        img.setPreserveRatio(true);
        img.setSmooth(true);
        img.fitWidthProperty().bind(getWidthProperty().subtract(30));
        img.fitHeightProperty().bind(getHeightProperty().divide(2));
        img.setOnMouseClicked(event -> {
            switchCenterObject.show(img, Side.RIGHT, 0, 0);
            //switchCenterObject.show(img, event.getX(), event.getY());
            //switchCenterObject.setX(event.getSceneX());
            //switchCenterObject.setY(event.getSceneY());
        });
        displayImage();
        centerContainer.getChildren().add(img);

        final Slider songSlider = new Slider(0, 1, 0);
        songSlider.getStyleClass().add("song");
        songSlider.prefHeightProperty().bind(getHeightProperty().divide(20));
        final ProgressBar songProgressBar = new ProgressBar(0);
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
                if (player != null) {
                    player.seek(Duration.seconds(songLength * songSlider.getValue()));
                }
            }
        });
        //TODO image ist nicht mittig!!!!!!
        playImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/icons/play.png", false));
        pauseImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/icons/pause.png", false));
        startStop = new ImageButton(playImage, true, true);
        startStop.setOnAction(e -> startStopSong());
        //startStop.prefHeightProperty().bind(startStop.widthProperty());
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
            if (player != null) {
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
                System.out.println(i + ": " + (magnitudes[i] + dBThreshold));
                audioData.getData().add(new XYChart.Data<>(Integer.toString(i), magnitudes[i] + dBThreshold));
            }
        };
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> audioBarChart = new BarChart<>(xAxis, yAxis);
        audioBarChart.setBarGap(0);
        audioBarChart.setCategoryGap(0);
        audioBarChart.setLegendVisible(false);
        audioBarChart.setAnimated(false);
        audioBarChart.setVerticalGridLinesVisible(false);
        audioBarChart.setHorizontalGridLinesVisible(false);
        audioBarChart.setHorizontalZeroLineVisible(false);
        audioBarChart.setVerticalZeroLineVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setTickLabelsVisible(false);
        audioBarChart.prefWidthProperty().bind(getWidthProperty().subtract(30));
        audioBarChart.prefHeightProperty().bind(getHeightProperty().divide(2));
        audioData = new XYChart.Series<>();
        audioData.setName("audioData");
        audioBarChart.getData().add(audioData);
        audioBarChart.setOnMouseClicked(event ->
                switchCenterObject.show(audioBarChart, Side.RIGHT, 0,0 )
        );

        toggleGroup.selectedToggleProperty().addListener((observableValue, oldVal, newVal) -> {
            RadioMenuItem selectedMenu = (RadioMenuItem) toggleGroup.getSelectedToggle();
            switch (selectedMenu.getText()) {
                case "Bild":
                    centerContainer.getChildren().clear();
                    centerContainer.getChildren().add(img);
                    chartIsVisible = false;
                    player.setAudioSpectrumListener(null);
                    break;
                case "Graph":
                    centerContainer.getChildren().clear();
                    centerContainer.getChildren().add(audioBarChart);
                    chartIsVisible = true;
                    player.setAudioSpectrumListener(audioSpectrumListener);
                    break;
                default:
                    centerContainer.getChildren().clear();
            }
        });

        VBox playerVBox = new VBox(labelSongName, centerContainer, mediaControlVBox);
        playerVBox.setAlignment(Pos.CENTER);
        playerVBox.setSpacing(10);
        showNodes(playerVBox);

    }

    @Override
    public Node get() {
        stage.setOnCloseRequest(windowEvent -> {
            chartIsVisible = false;
            player.setAudioSpectrumListener(null);
        });
        return super.get();
    }
    private void displayImage() {
        img.setImage(defaultImage);
    }

    private void startStopSong() {
        if (player == null) {
            return;
        }
        if (isPlaying()) {
            player.pause();
            startStop.switchImage(playImage);
            triggerStringListener("Stoppe Musik");
        } else {
            player.play();
            startStop.switchImage(pauseImage);
            triggerStringListener("Spiele: " + labelSongName.getText());
        }
    }

    private void reset(boolean andDispose) {
        if (player == null) {
            return;
        }
        player.stop();
        audioData.getData().clear();
        if (andDispose) {
            player.dispose();
        }
        startStop.switchImage(playImage);
    }

    private void updateSong(Song nextSong, boolean startPlaying) {
        if (nextSong == null) {
            return;
        }
        songHistoryStack.add(nextSong);
        reset(true);
        Path path = nextSong.getPath();
        labelSongName.setText(nextSong.getTitle());
        setDestroyListener(() -> SettingFile.saveLastSong(path));
        currentSong = new Media(Helper.p2uris(path));
        // TODO memory leak on Media/MediaPlayer ? i cant delete music files after they got played
        player = new MediaPlayer(currentSong);
        player.setOnEndOfMedia(this::skipforwards);
        player.setVolume(volume);

        //next song starts immediately or stops before
        if (startPlaying) {
            startStopSong();
        }
        activateListeners();
        triggerStringListener("Spiele: " + labelSongName.getText());
    }

    private void skipforwards() {
        if (playlist == null) {
            return;
        }
        updateSong(playlist.nextSong(onRepeat), true);
    }

    private void skipbackwards() {
        if (playlist == null) {
            return;
        }
        if (player != null && player.getCurrentTime().toSeconds() < 2 && songLength > 10) {
            // skip backwards to the song before
            updateSong(playlist.beforeSong(onRepeat), true);
        } else {
            // skip backwards to the beginning of the song
            updateSong(playlist.getLastPlayedSong(), true);
        }
    }

    private void skipTime(int timeInSeconds) {
        if (player == null) {
            return;
        }
        player.seek(new Duration(player.getCurrentTime().toMillis() + (timeInSeconds * 1000)));
    }

    private void activateListeners() {
        currentSong.durationProperty().addListener((arg0, arg1, duration) -> songLength = duration.toSeconds());
        player.currentTimeProperty().addListener(playerSongLengthListener);
        if (chartIsVisible) {
            player.setAudioSpectrumListener(audioSpectrumListener);
            player.setAudioSpectrumThreshold(-dBThreshold);
        }
    }

    public boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    void setPlaylist(Playlist playlist, boolean startPlaying) {
        this.playlist = playlist;
        updateSong(playlist.nextSong(onRepeat), startPlaying);
    }

    void setPlaylist(Song song, boolean startPlaying) {
        if (song == null || !song.isPlayable()) {
            return;
        }
        Playlist singleSongPlaylist = new Playlist();
        singleSongPlaylist.add(song);
        setPlaylist(singleSongPlaylist, startPlaying);
    }

    void setDynamicSize(Region region) {
        region.setMinWidth(Control.USE_PREF_SIZE);
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMinHeight(Control.USE_PREF_SIZE);
        region.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(region, Priority.SOMETIMES);
    }
}
