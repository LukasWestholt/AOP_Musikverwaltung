package musikverwaltung;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class HelloView extends GenericView {

    public HelloView(ScreenController sc) {
        super(sc);
    }

    public StackPane get() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        stackPane.getChildren().add(l);
        return stackPane;
    }
}
