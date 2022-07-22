package musikverwaltung;

import java.io.File;


import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class SongView extends GenericView{
	int currentIndex = 0;
	double songLength;
	double progress = 0;
	double volume = 0;
	boolean playing = false;
	Image defaultImage;
	ImageView viewer;
	String songName;
	ReadOnlyDoubleProperty width;
	ReadOnlyDoubleProperty height;
	Button startStop;
	Label labelSongName;
	Media currentSong;
	MediaPlayer player;
	private final ObservableList<Musikstueck> playlist;
	Runnable endOfSongAction;
	ChangeListener<Duration> songLengthListener;
	ChangeListener<Duration> pbarChanger;
	MapChangeListener<String, Object> metaDataListener;

	public SongView(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
		super(width, height);
		this.width = width;
		this.height = height;
		playlist = FXCollections.observableArrayList(
													 new Musikstueck("Fireflies", "OWL City", "Pop", ".\\media//song_test.m4a" ),
													 new Musikstueck("Deep Thoughts", "Neffex", "Rap", ".\\media//deep_thoughts_neffex.mp3"),
													 new Musikstueck("friendship forever", "?", "?", ".\\media//musicfox_friendship_forever.mp3")
		);				
	
		songName = "noch kein Title";
		File imgFile = new File(".\\media\\default_img.JPG");
		defaultImage = new Image(imgFile.toURI().toString());
		

	}
	
	public StackPane get() {
		/*  https://www.geeksforgeeks.org/javafx-progressbar/
		*	https://stackoverflow.com/questions/26850828/how-to-make-a-javafx-button-with-circle-shape-of-3xp-diameter
		*/
		
		viewer = new ImageView();
		viewer.fitHeightProperty().bind(height.divide(3));
		viewer.fitWidthProperty().bind(width.divide(2));
		displayImage();
		labelSongName = new Label(songName);
		labelSongName.setFont(Font.font(50));
		//labelSongName.setStyle("-fx-background-color: #001A91;");
		
		//was ist normal? variable mit 0 initilaisieren und dann einsetzen oder 0 einsetzen und später wenn variable einen nutzen hat diese einsetzen
		//durch funktion ersetzen
		File file = new File((playlist.get(0)).getpath());	
		currentSong = new Media(file.toURI().toString()); 
		player = new MediaPlayer(currentSong);
		player.setOnEndOfMedia(endOfSongAction);
		labelSongName.setText(songName);
		player.setVolume(0);
		
		ProgressBar pbar = new ProgressBar();
		pbar.prefHeightProperty().bind(height.divide(15));
		pbar.prefWidthProperty().bind(width.subtract(width.divide(20)));;
		pbar.setProgress(0);
		
		songLengthListener = new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> arg0, Duration arg1, Duration duration) {
				songLength = duration.toSeconds();
			}
		};
		pbarChanger = new ChangeListener<Duration>() {
			@Override
			public void changed(ObservableValue<? extends Duration> arg0, Duration arg1, Duration duration) {
				progress = duration.toSeconds()/songLength;
				pbar.setProgress(progress);
			}
		};	
		
		metaDataListener = new MapChangeListener<String, Object>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Object> metadata) {
				System.out.println(currentSong.getMetadata());
				songName = (metadata.getMap().get("title")).toString();
				System.out.println((metadata.getMap().get("title")).toString());
				labelSongName.setText(songName);
			}
			
		};
		
		endOfSongAction = new Runnable() {
			@Override
			public void run() {
				skipforwards();
			}
		}; 
		
		
		
		/*
		currentSong.durationProperty().addListener((property, old, duration) -> {
			songLength = duration.toSeconds();
		});
		
		player.currentTimeProperty().addListener((property, old, duration) -> {
			progress = duration.toSeconds()/songLength;
			pbar.setProgress(progress);
		});
		*/
		
		currentSong.durationProperty().addListener(songLengthListener);
		currentSong.getMetadata().addListener(metaDataListener);
		player.currentTimeProperty().addListener(pbarChanger);
		
		/*
	    currentSong.getMetadata().addListener(new MapChangeListener<String, Object>() {
			@Override
			public void onChanged(Change<? extends String, ? extends Object> metadata) {
				System.out.println(currentSong.getMetadata());
				songName = (metadata.getMap().get("title")).toString();
				System.out.println((metadata.getMap().get("title")).toString());
				labelSongName.setText(songName);
			}
	    });*/

	    startStop = new Button("start");
	    startStop.setShape(new Circle((width.divide(5).get())));
	    startStop.prefWidthProperty().bind(((width.greaterThan(height)).get() ? height.divide(5) : width.divide(5) ));
	    startStop.prefHeightProperty().bind(((width.greaterThan(height)).get() ? height.divide(5): width.divide(5) ));
	    startStop.setOnAction(e -> startStopSong());
	    
	    Button skipforward = new Button("skip +");
	    skipforward.prefWidthProperty().bind(width.divide(9));
	    skipforward.prefHeightProperty().bind(height.divide(8));
	    skipforward.setOnAction(e -> skipforwards());
	    
	    Button skipbackward = new Button("skip -");
	    skipbackward.setOnAction(e -> skipbackwards());
	    skipbackward.prefWidthProperty().bind(width.divide(9));
	    skipbackward.prefHeightProperty().bind(height.divide(8));
	    
	    Button reset = new Button("reset");
	    reset.setOnAction(e -> reset());
	    reset.prefWidthProperty().bind(width.divide(9));
	    reset.prefHeightProperty().bind(height.divide(8));
	    
	    Slider slider = new Slider(0, 1, 0);
	    slider.prefWidthProperty().bind(width.divide(30));
	    slider.prefHeightProperty().bind(height.divide(5));
	    slider.setOrientation(Orientation.VERTICAL);
        
       // slider.setPrefSize((height.divide(5)).get(), (width.divide(50)).get());
        slider.valueProperty().addListener((useless1, useless2, sliderValue) -> {
        	volume = sliderValue.doubleValue();
			player.setVolume(volume);
        });
		
		HBox lowerHBox = new HBox(reset,skipbackward, startStop, skipforward, slider);
		lowerHBox.setAlignment(Pos.CENTER);
		lowerHBox.setSpacing(5);
		//lowerHBox.setStyle("-fx-background-color: #FFFA9F;");
		//lowerHBox.setBackground(new Background(new BackgroundFill(Color.BEIGE, null, null)));
		lowerHBox.maxHeightProperty().bind(height.divide(3));
	    //vBox.setBackground(Color.web("#81c483"));
		HBox middleHBox = new HBox(pbar);
		middleHBox.setAlignment(Pos.BOTTOM_CENTER);
		middleHBox.setSpacing(10);
		middleHBox.setPrefHeight(200);
		middleHBox.maxHeightProperty().bind(height.divide(3));
		//middleHBox.setStyle("-fx-background-color: #00FA9F;");
		
		VBox upperVBox = new VBox(labelSongName, viewer);
		upperVBox.setAlignment(Pos.TOP_CENTER);
		upperVBox.maxHeightProperty().bind(height.divide(3));
		//upperVBox.setStyle("-fx-background-color: #00FA9F;");
		
		stackPane.getChildren().add(lowerHBox);
		stackPane.setAlignment(lowerHBox, Pos.BOTTOM_CENTER);
		stackPane.getChildren().add(middleHBox);
		stackPane.setAlignment(middleHBox, Pos.CENTER);
		stackPane.getChildren().add(upperVBox);
		stackPane.setAlignment(upperVBox, Pos.TOP_CENTER);
	    
		return stackPane;
	}
	
	private void displayImage() {
		viewer.setImage(defaultImage);
	}
	
	private void startStopSong() {
		if (playing) {
			player.pause();
			System.out.println(progress);
			startStop.setText("Start");
		} else {
			player.play();
			System.out.println(progress);
			startStop.setText("Stop");
			
		}
        playing = !playing;
	}
	
	private void reset() {
		player.stop();
		playing = false;
		startStop.setText("Start");
	}
	
	private void changeSong(int index) {
		if (player != null) {
			//reset();
			player.stop();
			player.dispose();
		}
		
		File file = new File((playlist.get(index)).getpath());	
		System.out.println(playlist.get(index).bekommeTitel());
		currentSong = new Media(file.toURI().toString()); 
		player = new MediaPlayer(currentSong);
		player.setOnEndOfMedia(endOfSongAction);
		//next song starts immediately or stops before 
		player.play();
		labelSongName.setText(songName);
		activateListeners();
	}
	
	private void skipforwards() {
		if (currentIndex < (playlist.size() - 1)) {
			currentIndex ++;
		} else {
			currentIndex = 0;
		}
		changeSong(currentIndex);
	}
	
	private void skipbackwards() {
		if (currentIndex > 0) {
			currentIndex --;
		} else {
			currentIndex = playlist.size() - 1;
		}
		changeSong(currentIndex);
	}
	
	private void activateListeners() {
		currentSong.durationProperty().addListener(songLengthListener);
		currentSong.getMetadata().addListener(metaDataListener);
		player.currentTimeProperty().addListener(pbarChanger);
	}
	
}
//

