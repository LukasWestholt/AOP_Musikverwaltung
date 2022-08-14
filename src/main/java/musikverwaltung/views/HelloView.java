package musikverwaltung.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import musikverwaltung.ScreenController;

public class HelloView extends GenericView {
    public HelloView(ScreenController sc) {
        super(sc);
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label title = new Label("Musikverwaltung");
        BorderPane borderPane = new BorderPane();
        Label bottomText = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion);
        title.getStyleClass().add("header");
        borderPane.setCenter(title);
        borderPane.setBottom(bottomText);
        BorderPane.setMargin(bottomText, new Insets(20));
        BorderPane.setAlignment(bottomText, Pos.CENTER);
        showNodes(borderPane);
    }
}
