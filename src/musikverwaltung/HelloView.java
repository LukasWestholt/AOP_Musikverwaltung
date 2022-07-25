package musikverwaltung;

import javafx.scene.control.Label;

public class HelloView extends GenericView {

    public HelloView(ScreenController sc) {
        super(sc);
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        l.getStyleClass().add("header");
        showNodes(l);
    }
}
