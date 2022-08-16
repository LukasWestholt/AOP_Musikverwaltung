package musikverwaltung.views;


import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.concurrent.Worker;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

        Button buttonCancel = new Button("Close");
        buttonCancel.setCancelButton(true);
        buttonCancel.setOnAction(e -> stage.close());
        HBox buttonHBox = new HBox(buttonCancel);
        VBox settingsVBox = new VBox(webView, buttonHBox);
        showNodes(settingsVBox);
    }
}
