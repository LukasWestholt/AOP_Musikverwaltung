package musikverwaltung;

import java.io.File;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SongView extends MenuBarView {
	int currentIndex = 0;
	double songLength;
	double volume = 0;
	Image defaultImage;
	ImageView img;
	Button startStop;
	Label labelSongName;
	Media currentSong;
	MediaPlayer player;
	private final ObservableList<Musikstueck> playlist = FXCollections.observableArrayList();

	private ChangeListener<Duration> playerSongLengthListener;

	Button mainViewButton = new Button("Musikverwaltung");
	Button resetButton = new Button("Reset");

	public SongView(ScreenController sc) {
		super(sc);

		File imgFile = new File(".\\media\\default_img.JPG");
		defaultImage = new Image(imgFile.toURI().toString());

		mainViewButton.setOnAction(e -> screenController.activate("Musikverwaltung"));
		resetButton.setOnAction(e -> reset(false));
		menuToolBar.getItems().addAll(mainViewButton, resetButton);
	}

	public StackPane get() {
		/*  https://www.geeksforgeeks.org/javafx-progressbar/
		*	https://stackoverflow.com/questions/26850828/how-to-make-a-javafx-button-with-circle-shape-of-3xp-diameter
		*/

		labelSongName = new Label("noch kein Title");
		labelSongName.setFont(Font.font(35));
		//labelSongName.setStyle("-fx-background-color: #001A91;");

		img = new ImageView();
		img.setPreserveRatio(true);
		img.setSmooth(true);
		img.fitWidthProperty().bind(stackPane.prefWidthProperty().subtract(30));
		VBox.setVgrow(img, Priority.SOMETIMES);
		displayImage();

		Pane spacePane = new Pane();
		VBox.setVgrow(spacePane, Priority.ALWAYS);

		final Slider songSlider = new Slider(0, 1, 0);
		songSlider.getStyleClass().add("song");
		songSlider.prefHeightProperty().bind(stackPane.prefHeightProperty().divide(20));
		final ProgressBar songProgressBar = new ProgressBar(0);
		songProgressBar.getStyleClass().add("song");
		songProgressBar.prefHeightProperty().bind(stackPane.prefHeightProperty().divide(20));
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
				if (player != null) player.seek(Duration.seconds(songLength * songSlider.getValue()));
			}
		});

		StackPane songSliderProgressbar = new StackPane();
		songSliderProgressbar.getChildren().addAll(songProgressBar, songSlider);

	    startStop = new Button("start");
		System.out.println(stackPane.prefWidthProperty().divide(5).get());
		startStop.setShape(new Circle(stackPane.prefWidthProperty().divide(5).get()));
	    startStop.prefWidthProperty().bind(stackPane.prefWidthProperty().divide(3));
	    startStop.prefHeightProperty().bind(startStop.prefWidthProperty());
	    startStop.setOnAction(e -> startStopSong());
		setDynamicSize(startStop);

	    Button skipforward = new Button("skip +");
	    skipforward.setOnAction(e -> skipforwards());
		setDynamicSize(skipforward);

	    Button skipbackward = new Button("skip -");
	    skipbackward.setOnAction(e -> skipbackwards());
		setDynamicSize(skipbackward);

	    Slider slider = new Slider(0, 1, 0);
        slider.valueProperty().addListener((useless1, useless2, sliderValue) -> {
        	volume = sliderValue.doubleValue();
			if (player != null) player.setVolume(volume);
        });

		HBox buttonHBox = new HBox(skipbackward, startStop, skipforward);
		buttonHBox.setAlignment(Pos.CENTER);
		buttonHBox.setSpacing(10);
		VBox buttonVBox = new VBox(songSliderProgressbar, buttonHBox, slider);
		buttonVBox.setAlignment(Pos.CENTER);
		buttonVBox.setSpacing(10);
		buttonVBox.setBackground(new Background(new BackgroundFill(Color.BEIGE, null, null)));
		VBox.setMargin(buttonVBox, new Insets(0, 15, 15, 15));

		VBox vBox = new VBox(menuToolBar, labelSongName, img, spacePane, buttonVBox);
		vBox.setAlignment(Pos.CENTER);
		vBox.setSpacing(10);
		stackPane.getChildren().add(vBox);
		return stackPane;
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
			startStop.setText("Start");
		} else {
			player.play();
			startStop.setText("Stop");
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
		startStop.setText("Start");
	}

	private void updateSong() {
		if (currentIndex + 1 > playlist.size()) {
			currentIndex = 0;
		}
		if (playlist.size() == 0) {
			return;
		}
		reset(true);

		File file = playlist.get(currentIndex).getPath();
		labelSongName.setText(playlist.get(currentIndex).bekommeTitel());
		currentSong = new Media(file.toURI().toString());
		player = new MediaPlayer(currentSong);
		player.setOnEndOfMedia(this::skipforwards);
		player.setVolume(volume);
		//next song starts immediately or stops before
		startStopSong();
		activateListeners();
	}

	private void skipforwards() {
		if (currentIndex < (playlist.size() - 1)) {
			currentIndex ++;
		} else {
			currentIndex = 0;
		}
		updateSong();
	}

	private void skipbackwards() {
		if (currentIndex > 0) {
			currentIndex --;
		} else if (playlist.size() != 0) {
			currentIndex = playlist.size() - 1;
		}
		updateSong();
	}

	private void activateListeners() {
		currentSong.durationProperty().addListener((arg0, arg1, duration) -> songLength = duration.toSeconds());
		currentSong.getMetadata().addListener((MapChangeListener<String, Object>) metadata -> {
			System.out.println(currentSong.getMetadata());
			Object titel = metadata.getMap().get("title");
			if (titel != null) {
				System.out.println(titel);
				labelSongName.setText(titel.toString());
			}

		});
		player.currentTimeProperty().addListener(playerSongLengthListener);
	}

	private boolean isPlaying() {
		return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
	}

	void setPlaylist(List<Musikstueck> playlist) {
		this.playlist.clear();
		this.playlist.addAll(playlist);
		updateSong();
	}

	void setDynamicSize(Region region) {
		region.setMinWidth(Control.USE_PREF_SIZE);
		region.setMaxWidth(Double.MAX_VALUE);
		region.setMinHeight(Control.USE_PREF_SIZE);
		region.setMaxHeight(Double.MAX_VALUE);
		HBox.setHgrow(region, Priority.SOMETIMES);
	}
}
