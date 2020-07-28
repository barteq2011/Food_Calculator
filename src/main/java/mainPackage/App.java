package mainPackage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainPackage.dataOperating.ProductData;

import java.io.IOException;

public final class App extends Application {
    double XOffset, YOffset;
    // Handlers for draggable window
    EventHandler<MouseEvent> dragHandler, pressHandler;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Calculating new window position
        dragHandler = mouseEvent -> {
            primaryStage.setX(mouseEvent.getScreenX() - XOffset);
            primaryStage.setY(mouseEvent.getScreenY() - YOffset);
        };
        // Getting window properties
        pressHandler = mouseEvent -> {
            XOffset = mouseEvent.getSceneX();
            YOffset = mouseEvent.getSceneY();
        };
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        // Setting draggable window area
        controller.getMenuBar().setOnMousePressed(pressHandler);
        controller.getMenuBar().setOnMouseDragged(dragHandler);
        root.setOnMousePressed(pressHandler);
        root.setOnMouseDragged(dragHandler);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image(getClass().getResource("styles/img/App.png").toExternalForm()));
        primaryStage.setOpacity(0.95);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Food Calculator 2.0");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    @Override
    public void init() {
        // Open connection with database, if failed function will return false
        if (!ProductData.getInstance().open())
            Platform.exit();
    }

    // Close connection with database before close app
    @Override
    public void stop() {
        ProductData.getInstance().close();
    }

    public static void main(String[] args) {
        launch();
    }

}