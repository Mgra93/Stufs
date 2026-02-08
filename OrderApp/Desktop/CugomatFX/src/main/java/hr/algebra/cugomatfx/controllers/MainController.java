package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.enums.UserRole;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.helpers.Navigator;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button btnTable;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnWorker;
    @FXML
    private Button btnProduct;
    @FXML
    private Button btnCategory;
    @FXML
    private StackPane spContent;
    @FXML
    private MenuItem miUser;
    @FXML
    private MenuItem miSetting;
    @FXML
    private MenuItem miLogout;
    private Object currentController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPaneWithClose(Navigator.TABLE_VIEW_PATH);
        setItemsText();
        initButtons();
        setVisibleByRole();
    }

    private void setVisibleByRole() {
        UserRole role = UserRole.fromString(ApiService.getInstance().getRole());

        if (role != UserRole.ROLE_ADMIN) {
            btnWorker.setVisible(false);
            btnWorker.setManaged(false);

            btnProduct.setVisible(false);
            btnProduct.setManaged(false);

            btnCategory.setVisible(false);
            btnCategory.setManaged(false);
        }
    }

    private void setItemsText() {
        setUserLabel();
        btnTable.setText(MessageHelper.getString("main.btn.table"));
        btnOrder.setText(MessageHelper.getString("main.btn.orders"));
        btnWorker.setText(MessageHelper.getString("main.btn.worker"));
        btnProduct.setText(MessageHelper.getString("main.btn.product"));
        btnCategory.setText(MessageHelper.getString("main.btn.category"));
        miSetting.setText(MessageHelper.getString("main.btn.setting"));
        miLogout.setText(MessageHelper.getString("main.btn.logout"));
    }

    private void initButtons() {
        btnTable.setOnAction(e -> loadPaneWithClose(Navigator.TABLE_VIEW_PATH));
        btnOrder.setOnAction(e -> loadPaneWithClose(Navigator.ORDER_PREV_VIEW_PATH));
        btnWorker.setOnAction(e -> loadPaneWithClose(Navigator.WORKER_PREV_VIEW_PATH));
        btnProduct.setOnAction(e -> loadPaneWithClose(Navigator.PRODUCT_PREV_VIEW_PATH));
        btnCategory.setOnAction(e -> loadPaneWithClose(Navigator.CATEGORY_PREV_VIEW_PATH));
        miSetting.setOnAction(e -> loadPaneWithClose(Navigator.SETTINGS_VIEW_PATH));
        miLogout.setOnAction(e -> logout());
    }

    private void setUserLabel() {
        if (CugomatFXApplication.getCurrentWorker() != null && CugomatFXApplication.getCurrentWorker().getUser() != null) {
            String fullName = "";

            if (CugomatFXApplication.getCurrentWorker().getUser().getFirstName() != null) {
                fullName += CugomatFXApplication.getCurrentWorker().getUser().getFirstName() + " ";
            }

            if (CugomatFXApplication.getCurrentWorker().getUser().getLastName() != null) {
                fullName += CugomatFXApplication.getCurrentWorker().getUser().getLastName();
            }

            miUser.setText(fullName);
        }
    }

    private void logout() {
        CugomatFXApplication.setCurrentWorker(null);
        ApiService.getInstance().clearAccessData();
        Navigator.loadScene(Navigator.LOGIN_VIEW_PATH);
    }

    private void closeCurrentController() {
        if (currentController != null && currentController instanceof TableController tableController) {
            tableController.onCloseView();
        }
    }

    private void loadPaneWithClose(String fxmlPath) {
        closeCurrentController();
        currentController = Navigator.loadPaneWithController(spContent, fxmlPath);
    }
}
