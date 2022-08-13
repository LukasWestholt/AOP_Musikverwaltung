package musikverwaltung.views;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import musikverwaltung.ScreenController;

public class MenuBarView extends GenericView {
    //doch benutzen!
    private final ToolBar menuToolBar = new ToolBar();
    private final VBox wrapperVBox = new VBox();

    final Button mainViewButton = new Button("Musikverwaltung");
    final Button playlistViewButton = new Button("Playlist");
    final Button settingViewButton = new Button("Einstellungen");
    final Button creditsViewButton = new Button("Credits");

    public MenuBarView(ScreenController sc, double prefWidth, double prefHeight) {
        super(sc, prefWidth, prefHeight);
        addMenuItems(true, mainViewButton, playlistViewButton, settingViewButton, creditsViewButton);
        wrapperVBox.getChildren().addAll(menuToolBar, stackPane);
    }

    public MenuBarView(ScreenController sc) {
        this(sc, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public void addActiveMenuButton(Button button, EventHandler<ActionEvent> eventHandler) {
        button.setDisable(false);
        button.setOnAction(eventHandler);
        if (!menuToolBar.getItems().contains(button)) {
            menuToolBar.getItems().add(button);
        }
    }

    public void setActiveMenuItem(Region region) {
        region.setId("active");
    }

    private void addMenuItems(boolean disable, Region... regions) {
        for (Region region : regions) {
            region.setDisable(disable);
            if (!menuToolBar.getItems().contains(region)) {
                menuToolBar.getItems().add(region);
            }
        }
    }

    public void ignoreMenuItems(Region... regions) {
        menuToolBar.getItems().removeAll(regions);
    }

    @Override
    public Node get() {
        super.get();
        // LW: Prevent the menu bar from always getting the focus and put the focus on the content.
        Platform.runLater(stackPane::requestFocus);
        return wrapperVBox;
    }

    @Override
    public void bindSceneDimensions(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        wrapperVBox.prefWidthProperty().bind(width);
        wrapperVBox.prefHeightProperty().bind(height);
        stackPane.prefWidthProperty().bind(getWidthProperty());
        stackPane.prefHeightProperty().bind(getHeightProperty());
    }

    @Override
    public DoubleBinding getWidthProperty() {
        return wrapperVBox.prefWidthProperty().subtract(0);
    }

    @Override
    public DoubleBinding getHeightProperty() {
        return wrapperVBox.prefHeightProperty().subtract(menuToolBar.heightProperty());
    }
}
