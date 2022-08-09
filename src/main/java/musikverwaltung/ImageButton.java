package musikverwaltung;

import java.io.File;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ImageButton extends Button {
    public ImageButton(File file, boolean isCircle, boolean withoutButtonStyle) {
        super();
        if (withoutButtonStyle) {
            getStyleClass().clear();
        }
        if (isCircle) {
            setShape(new Circle(1));
        }
        final Image image = new Image(file.toURI().toString(), true);
        ImageView imageView = new ImageView(image);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.fitHeightProperty().bind(prefHeightProperty());
        imageView.setPreserveRatio(true);
        setGraphic(imageView);
    }
}
