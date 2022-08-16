package musikverwaltung.views;


import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import musikverwaltung.Helper;
import musikverwaltung.ScreenController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

public class CreditsView extends GenericView {
    public CreditsView(ScreenController sc) {
        super(sc, 350, 300);
        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        WebEngine webEngine = webView.getEngine();
        webEngine.load(Helper.getResourcePathURIS(this.getClass(), "/credits.html", false).toString());
        //webEngine.setUserStyleSheetLocation(getClass().getResource("/style.css").toExternalForm());
        // TODO scrollBar hides text
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED.equals(newValue)) {
                Document document = webEngine.getDocument();
                NodeList nodeList = document.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    EventTarget eventTarget = (EventTarget) node;
                    eventTarget.addEventListener("click", evt -> {
                        HTMLAnchorElement anchorElement = (HTMLAnchorElement) evt.getCurrentTarget();
                        String href = anchorElement.getHref();
                        if (href != null && Desktop.isDesktopSupported()) {
                            try {
                                Desktop.getDesktop().browse(
                                        new URI(href));
                            } catch (IOException | URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        evt.preventDefault();
                    }, false);
                }
            }
        });

        Button buttonCancel = new Button("Beenden");
        buttonCancel.setCancelButton(true);
        buttonCancel.setOnAction(e -> stage.close());
        HBox buttonHBox = new HBox(buttonCancel);
        VBox creditsVbox = new VBox(webView, buttonHBox);
        //TODO macht nichts
        Rectangle background = new Rectangle();
        background.setFill(Color.rgb(142, 196, 117));
        System.out.println(background.getFill());

        StackPane.setAlignment(background, Pos.TOP_LEFT);
        StackPane.setAlignment(creditsVbox, Pos.TOP_LEFT);

        showNodes(background, creditsVbox);
    }
}
