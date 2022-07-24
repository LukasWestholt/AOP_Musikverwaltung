package musikverwaltung;

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

public class MenuBarView extends GenericView {

    private final ToolBar menuToolBar = new ToolBar();
    private final VBox wrapperVBox = new VBox();

    Button mainViewButton = new Button("Musikverwaltung");
    Button playlistButton = new Button("Playlist");
    Button settingButton = new Button("Einstellungen");

    public MenuBarView(ScreenController sc) {
        super(sc);
        addMenuItems(true, mainViewButton, playlistButton, settingButton);
        wrapperVBox.getChildren().addAll(menuToolBar, stackPane);
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
        System.out.println("prevent");
        stackPane.requestFocus();
        Platform.runLater(stackPane::requestFocus);

        return wrapperVBox;
    }

    @Override
    public void bindSceneDimensions(ReadOnlyDoubleProperty width, ReadOnlyDoubleProperty height) {
        wrapperVBox.prefWidthProperty().bind(width);
        wrapperVBox.prefHeightProperty().bind(height);
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
