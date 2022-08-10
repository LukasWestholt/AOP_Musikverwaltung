package musikverwaltung;

import java.nio.file.Path;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ImageButton extends Button {
    ImageView imageView = new ImageView();

    public ImageButton(Image image, boolean isCircle, boolean withoutButtonStyle) {
        super();
        if (withoutButtonStyle) {
            getStyleClass().clear();
        }
        if (isCircle) {
            setShape(new Circle(1));
        }
        switchImage(image);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.fitHeightProperty().bind(prefHeightProperty());
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
    }

    public ImageButton(Path path, boolean isCircle, boolean withoutButtonStyle) {
        this(new Image(Helper.p2uris(path), true), isCircle, withoutButtonStyle);
    }

    public void switchImage(Image image) {
        if (image.isError()) {
            System.out.println("image has error");
        }
        imageView.setImage(image);
    }
}
