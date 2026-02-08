package hr.algebra.cugomatfx;

import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.helpers.Navigator;
import hr.algebra.cugomatfx.models.Worker;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class CugomatFXApplication extends Application {
    private static Worker worker;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle(MessageHelper.getString("login.app.title"));
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        Navigator.setStage(stage);
        Navigator.loadScene(Navigator.LOGIN_VIEW_PATH);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setCurrentWorker(Worker w) {
        worker = w;
    }

    public static Worker getCurrentWorker() {
        return worker;
    }
}