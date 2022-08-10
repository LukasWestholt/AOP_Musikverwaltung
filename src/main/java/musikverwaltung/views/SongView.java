package musikverwaltung.views;

import java.nio.file.Path;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import musikverwaltung.Helper;
import musikverwaltung.Playlist;
import musikverwaltung.ScreenController;
import musikverwaltung.SettingFile;
import musikverwaltung.handler.StringListenerManager;

public class SongView extends MenuBarView implements StringListenerManager {
    int currentIndex = 0;
    double songLength;
    double volume = 0.5;
    final Image defaultImage;
    final ImageView img;
    final Button startStop;
    final Background playBackground;
    final Background pauseBackground;
    final Label labelSongName;
    Media currentSong;
    MediaPlayer player;
    private Playlist playlist = new Playlist();
    private final ChangeListener<Duration> playerSongLengthListener;

    public SongView(ScreenController sc) {
        /*
        https://www.geeksforgeeks.org/javafx-progressbar/
        https://stackoverflow.com/questions/26850828/how-to-make-a-javafx-button-with-circle-shape-of-3xp-diameter
         */
        super(sc, 320, 560);
        defaultImage = new Image(Helper.getResourcePathString(this.getClass(), "/default_img.jpg", false));

        addActiveMenuButton(mainViewButton,
                e -> screenController.activate(MainView.class)
        );
        addActiveMenuButton(new Button("Reset"),
                e -> reset(false)
        );
        ignoreMenuItems(settingButton, playlistButton, creditsButton);

        labelSongName = new Label("Unbekannt");
        labelSongName.getStyleClass().add("header");

        img = new ImageView();
        img.setPreserveRatio(true);
        img.setSmooth(true);
        img.fitWidthProperty().bind(getWidthProperty().subtract(30));
        img.fitHeightProperty().bind(getHeightProperty().divide(2));
        displayImage();

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

        startStop = new Button();
        Image playImage = new Image(Helper.getResourcePathString(this.getClass(), "/icons/play.png", false));
        Image pauseImage = new Image(Helper.getResourcePathString(this.getClass(), "/icons/pause.png", false));
        playBackground = new Background(new BackgroundFill(new ImagePattern(playImage), null, null));
        pauseBackground = new Background(new BackgroundFill(new ImagePattern(pauseImage), null, null));
        startStop.getStyleClass().clear();
        startStop.setBackground(playBackground);
        startStop.setShape(new Circle(1));
        startStop.setOnAction(e -> startStopSong());
        startStop.prefHeightProperty().bind(startStop.widthProperty());
        startStop.setMaxWidth(Double.MAX_VALUE);
        startStop.maxHeightProperty().bind(startStop.widthProperty());
        HBox.setHgrow(startStop, Priority.SOMETIMES);

        Button skipForward = new Button("skip +");
        skipForward.setOnAction(e -> skipforwards());
        setDynamicSize(skipForward);
        skipForward.maxHeightProperty().bind(startStop.widthProperty().multiply(0.7));

        Button skipBackward = new Button("skip -");
        skipBackward.setOnAction(e -> skipbackwards());
        setDynamicSize(skipBackward);
        skipBackward.maxHeightProperty().bind(startStop.widthProperty().multiply(0.7));

        startStop.minWidthProperty().bind(
                Bindings.max(skipBackward.heightProperty(), skipForward.heightProperty())
        );

        HBox buttonHBox = new HBox(skipBackward, startStop, skipForward);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.setSpacing(10);
        buttonHBox.maxWidthProperty().bind(getHeightProperty().divide(2));

        Slider slider = new Slider(0, 1, 0);
        slider.setValue(volume);
        slider.setMinorTickCount(10);
        slider.setMajorTickUnit(10.0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
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

        StackPane imgContainer = new StackPane();
        imgContainer.getChildren().add(img);
        imgContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(imgContainer, Priority.ALWAYS);

        VBox playerVBox = new VBox(labelSongName, imgContainer, mediaControlVBox);
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
            startStop.setBackground(playBackground);
            triggerStringListener("Stoppe Musik");
        } else {
            player.play();
            startStop.setBackground(pauseBackground);
            triggerStringListener("Spiele: " + labelSongName.getText());
        }
    }

    private void reset(boolean andDispose) {
        if (player == null) {
            return;
        }
        player.stop();
        if (andDispose) {
            player.dispose();
        }
        startStop.setBackground(playBackground);
    }

    private void updateSong(boolean startPlaying) {
        if (currentIndex + 1 > playlist.size()) {
            currentIndex = 0;
        }
        if (playlist.size() == 0) {
            return;
        }
        reset(true);

        Path path = playlist.get(currentIndex).getPath();
        labelSongName.setText(playlist.get(currentIndex).getTitle());
        setDestroyListener(() -> SettingFile.setLastSong(path));
        currentSong = new Media(Helper.p2s(path));
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
        if (currentIndex < (playlist.size() - 1)) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        updateSong(true);
    }

    private void skipbackwards() {
        if (currentIndex > 0) {
            currentIndex--;
        } else if (playlist.size() != 0) {
            currentIndex = playlist.size() - 1;
        }
        updateSong(true);
    }

    private void activateListeners() {
        currentSong.durationProperty().addListener((arg0, arg1, duration) -> songLength = duration.toSeconds());
        player.currentTimeProperty().addListener(playerSongLengthListener);
    }

    private boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    void setPlaylist(Playlist newPlaylist, boolean startPlaying) {
        this.playlist = newPlaylist;
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
