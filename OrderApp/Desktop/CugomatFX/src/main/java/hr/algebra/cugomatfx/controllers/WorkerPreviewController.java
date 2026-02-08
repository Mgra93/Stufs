package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.helpers.Navigator;
import hr.algebra.cugomatfx.models.Worker;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WorkerPreviewController implements Initializable {
    @FXML
    private Button btnCreateView;
    @FXML
    private Button btnUpdateView;
    @FXML
    private Button btnDelete;
    @FXML
    private TableView<Worker> tvWorker;
    @FXML
    private TableColumn<Worker, String> colUsername;
    @FXML
    private TableColumn<Worker, String> colFirstName;
    @FXML
    private TableColumn<Worker, String> colLastName;
    @FXML
    private TableColumn<Worker, String> colEmail;
    @FXML
    private TableColumn<Worker, String> colPhone;
    @FXML
    private TableColumn<Worker, String> colRole;
    @FXML
    private Label lblTitle;
    @FXML
    private StackPane spContent;

    private Worker selectedWorker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTexts();
        initButtons();
        setTable();
        loadWorkers();
    }

    private void setTexts() {
        btnCreateView.setText(MessageHelper.getString("worker.preview.btn.create.view"));
        btnUpdateView.setText(MessageHelper.getString("worker.preview.btn.update.view"));
        btnDelete.setText(MessageHelper.getString("worker.preview.btn.delete"));

        colUsername.setText(MessageHelper.getString("worker.preview.col.user.name"));
        colFirstName.setText(MessageHelper.getString("worker.preview.col.first.name"));
        colLastName.setText(MessageHelper.getString("worker.preview.col.last.name"));
        colEmail.setText(MessageHelper.getString("worker.preview.col.email"));
        colPhone.setText(MessageHelper.getString("worker.preview.col.phone"));

        lblTitle.setText(MessageHelper.getString("worker.preview.lbl.title"));
    }

    private void initButtons() {
        btnCreateView.setOnAction(e -> {
            WorkerFormController controller = Navigator.loadPaneWithController(spContent, Navigator.WORKER_FORM_VIEW_PATH);

            if (controller != null) {
                controller.setWorker(null);
                controller.setOnCloseCallback(() -> {
                    loadWorkers();
                    spContent.getChildren().clear();
                });
            }
        });

        btnUpdateView.setOnAction(e -> {
            if (selectedWorker != null) {
                WorkerFormController controller = Navigator.loadPaneWithController(spContent, Navigator.WORKER_FORM_VIEW_PATH);

                if (controller != null) {
                    controller.setWorker(selectedWorker);
                    controller.setOnCloseCallback(() -> {
                        loadWorkers();
                        spContent.getChildren().clear();
                    });
                }
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("worker.preview.select"), AlertLevel.INFO);
            }
        });

        btnDelete.setOnAction(e -> {
            if (selectedWorker != null) {
                deleteWorker(selectedWorker);
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("worker.preview.select"), AlertLevel.INFO);
            }
        });
    }

    private void setTable() {
        colUsername.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getUser().getUsername() != null ? o.getValue().getUser().getUsername() : "-"));
        colFirstName.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getUser().getFirstName() != null ? o.getValue().getUser().getFirstName() : "-"));
        colLastName.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getUser().getLastName() != null ? o.getValue().getUser().getLastName() : "-"));
        colEmail.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getUser().getEmail() != null ? o.getValue().getUser().getEmail() : "-"));
        colPhone.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getUser().getPhone() != null ? o.getValue().getUser().getPhone() : "-"));
        tvWorker.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tvWorker.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selection) -> {
            selectedWorker = selection;
        });

        tvWorker.setPlaceholder(new Label(MessageHelper.getString("worker.preview.tv.worker.prop")));
    }

    private void loadWorkers() {
        new Thread(() -> {
            try {
                List<Worker> workerList = ApiService.getInstance().getWorkerList();
                if (workerList == null) workerList = List.of();
                List<Worker> finalWorkers = workerList;
                Platform.runLater(() -> {
                    tvWorker.getItems().clear();
                    tvWorker.getItems().setAll(finalWorkers);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void deleteWorker(Worker worker) {
        new Thread(() -> {
            try {
                boolean deleted = ApiService.getInstance().deleteWorker(worker.getId());
                if (deleted) {
                    DialogHelper.showAlert(MessageHelper.getString("msg.info"), MessageHelper.getString("worker.preview.deleted"), AlertLevel.INFO);
                    loadWorkers();
                } else {
                    DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("worker.preview.delete.error"), AlertLevel.ERROR);
                }
            } catch (Exception e) {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("worker.preview.delete.error"), AlertLevel.ERROR);
            }
        }).start();
    }
}