package hr.algebra.cugomatfx.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
    private static Stage mainStage;

    public static final String DEFAULT_VIEW_PATH = "/hr/algebra/cugomatfx/forms";
    public static final String TABLE_VIEW_PATH = DEFAULT_VIEW_PATH + "/tables.fxml";
    public static final String SETTINGS_VIEW_PATH = DEFAULT_VIEW_PATH + "/settings.fxml";
    public static final String MAIN_VIEW_PATH = DEFAULT_VIEW_PATH + "/main.fxml";
    public static final String LOGIN_VIEW_PATH = DEFAULT_VIEW_PATH + "/login.fxml";

    public static final String ORDER_PREV_VIEW_PATH = DEFAULT_VIEW_PATH + "/orderPreview.fxml";

    public static final String CATEGORY_PREV_VIEW_PATH = DEFAULT_VIEW_PATH + "/categoryPreview.fxml";
    public static final String CATEGORY_FORM_VIEW_PATH = DEFAULT_VIEW_PATH + "/categoryForm.fxml";

    public static final String PRODUCT_PREV_VIEW_PATH = DEFAULT_VIEW_PATH + "/productPreview.fxml";
    public static final String PRODUCT_FORM_VIEW_PATH = DEFAULT_VIEW_PATH + "/productForm.fxml";

    public static final String WORKER_PREV_VIEW_PATH = DEFAULT_VIEW_PATH + "/workerPreview.fxml";
    public static final String WORKER_FORM_VIEW_PATH = DEFAULT_VIEW_PATH + "/workerForm.fxml";

    public static final String DEFAULT_IMAGE_PATH = "/hr/algebra/cugomatfx/images";
    public static final String INSIDE_TABLE_IMG_PATH = DEFAULT_IMAGE_PATH + "/table_inside.png";
    public static final String OUTSIDE_TABLE_IMG_PATH = DEFAULT_IMAGE_PATH + "/table_outside.png";
    public static final String BAR_TABLE_IMG_PATH = DEFAULT_IMAGE_PATH + "/bar_stool.png";
    public static final String BEER_IMG_PATH = DEFAULT_IMAGE_PATH + "/beer.png";

    public static void setStage(Stage stage){
        mainStage = stage;
    }

    public static void loadScene(String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            mainStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadPane(StackPane container, String fxml){
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            Node node = loader.load();
            container.getChildren().clear();
            container.getChildren().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> T loadPaneWithController(StackPane container, String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
            Node node = loader.load();
            container.getChildren().clear();
            container.getChildren().add(node);

            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
