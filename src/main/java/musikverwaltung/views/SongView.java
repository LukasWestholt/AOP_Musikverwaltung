package musikverwaltung.views;

import java.nio.file.Path;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    int currentIndex = 0;
    double songLength;
    double volume = 0.5;
    final Image defaultImage;
    final ImageView img;
    final ImageButton startStop;
    final Image playImage;
    final Image pauseImage;
    final Label labelSongName;
    Media currentSong;
    MediaPlayer player;

    final int dBthreshold = 60;
    XYChart.Series<String,Number>  audioData;
    AudioSpectrumListener audioSpectrumListener;
    private FilteredList<Song> playableSongs = new FilteredList<>(FXCollections.observableArrayList());
    private final ChangeListener<Duration> playerSongLengthListener;

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
        centerContainer .setAlignment(Pos.CENTER);
        VBox.setVgrow( centerContainer , Priority.ALWAYS);

        ContextMenu switchCenterObject = new ContextMenu();
        MenuItem headerMenu = new MenuItem("Ansicht wechseln:");
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
            //TODO set correct position
            switchCenterObject.show(img, event.getSceneX(), event.getSceneY());
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
        //TODO image ist nicht mittig
        playImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/icons/play.png", false));
        pauseImage = new Image(Helper.getResourcePathUriString(this.getClass(), "/icons/pause.png", false));
        startStop = new ImageButton(playImage, true, true);
        startStop.setOnAction(e -> startStopSong());
        //startStop.prefHeightProperty().bind(startStop.widthProperty());
        startStop.setPrefSize(30, 30);
        startStop.setMaxWidth(Double.MAX_VALUE);
        startStop.maxHeightProperty().bind(startStop.widthProperty());
        /* TODO wofür ist das?
        region.setMinWidth(Control.USE_PREF_SIZE);
        region.setMinHeight(Control.USE_PREF_SIZE);
        region.setMaxHeight(Double.MAX_VALUE);
        */

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
        /*mir gefällt es besser ohne
        slider.setMinorTickCount(10);
        slider.setMajorTickUnit(10.0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        */
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
                //System.out.println(i + ": " + (magnitudes[i] + dBthreshold));
                audioData.getData().add(new XYChart.Data<>(Integer.toString(i), magnitudes[i] + dBthreshold));
            }
        };
        //TODO listener soll aufhören wenn player fenster nicht mehr zusehen ist
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> audioBarChart = new BarChart<>(xAxis,yAxis);
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
        //audioBarChart.setMaxSize(900, 400);
        //audioBarChart.setMinSize(900, 400);
        audioData =new XYChart.Series<> ();
        audioData.setName("audioData");
        audioBarChart.getData().add(audioData);
        //TODO set correct position siehe oben
        audioBarChart.setOnMouseClicked(event -> switchCenterObject.show(audioBarChart, event.getSceneX(), event.getSceneY()));
        //TODO listener nur wenn graph gezeigt wird
        toggleGroup.selectedToggleProperty().addListener((observableValue, oldVal, newVal) -> {
            RadioMenuItem selectedMenu = (RadioMenuItem) toggleGroup.getSelectedToggle();
            switch (selectedMenu.getText()) {
                case "Bild":
                    centerContainer.getChildren().clear();
                    centerContainer.getChildren().add(img);
                    break;
                case "Graph":
                    centerContainer.getChildren().clear();
                    centerContainer.getChildren().add(audioBarChart);
                    break;
            }
        });

        VBox playerVBox = new VBox(labelSongName,  centerContainer , mediaControlVBox);
        playerVBox.setAlignment(Pos.CENTER);
        playerVBox.setSpacing(10);
        showNodes(playerVBox);
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
            //TODO soll bei stop einfach graph freezen?
            //audioData.getData().clear();
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

    private void updateSong(boolean startPlaying) {
        if (currentIndex + 1 > playableSongs.size()) {
            currentIndex = 0;
        }
        if (playableSongs.size() == 0) {
            return;
        }
        reset(true);
        Song song = playableSongs.get(currentIndex);
        Path path = song.getPath();
        labelSongName.setText(song.getTitle());
        setDestroyListener(() -> SettingFile.saveLastSong(path));
        currentSong = new Media(Helper.p2uris(path));
        player = new MediaPlayer(currentSong);
        player.setOnEndOfMedia(this::skipforwards);
        player.setVolume(volume);
        //player.setAudioSpectrumNumBands(10);
        player.setAudioSpectrumThreshold(-dBthreshold);

        //next song starts immediately or stops before
        if (startPlaying) {
            startStopSong();
        }
        activateListeners();
        triggerStringListener("Spiele: " + labelSongName.getText());
    }

    private void skipforwards() {
        if (currentIndex < (playableSongs.size() - 1)) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateSong(true);
    }

    private void skipbackwards() {
        if (currentIndex > 0) {
            currentIndex--;
        } else if (playableSongs.size() != 0) {
            currentIndex = playableSongs.size() - 1;
        }
        updateSong(true);
    }

    private void skipTime(int timeInSeconds) {
        player.seek(new Duration(player.getCurrentTime().toMillis() + (timeInSeconds * 1000)));
    }

    private void activateListeners() {
        currentSong.durationProperty().addListener((arg0, arg1, duration) -> songLength = duration.toSeconds());
        player.currentTimeProperty().addListener(playerSongLengthListener);
        player.setAudioSpectrumListener(audioSpectrumListener);
    }

    private boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    void setPlaylist(Playlist newPlaylist, boolean startPlaying) {
        this.playableSongs = newPlaylist.getAllPlayable();
        updateSong(startPlaying);
    }

    void setDynamicSize(Region region) {
        region.setMinWidth(Control.USE_PREF_SIZE);
        region.setMaxWidth(Double.MAX_VALUE);
        region.setMinHeight(Control.USE_PREF_SIZE);
        region.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(region, Priority.SOMETIMES);
    }
}
