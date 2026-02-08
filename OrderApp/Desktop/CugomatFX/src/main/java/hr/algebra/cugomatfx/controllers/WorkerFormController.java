package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.InputValidatorHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.models.Client;
import hr.algebra.cugomatfx.models.User;
import hr.algebra.cugomatfx.models.Worker;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class WorkerFormController implements Initializable {
    @FXML
    private Button btnAction;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblPassword;
    @FXML
    private Label lblPasswordConfirm;
    @FXML
    private Label lblFirstName;
    @FXML
    private Label lblLastName;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblPhone;
    @FXML
    private Label lblError;
    @FXML
    private TextField tfUserName;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private PasswordField pfPasswordConfirm;
    @FXML
    private TextField tfFirstName;
    @FXML
    private TextField tfLastName;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfPhone;
    @FXML
    private VBox vbPassword;
    @FXML
    private VBox vbPasswordConfirm;
    @FXML
    private HBox hbPasswordCheck;
    @FXML
    private CheckBox cbChangePassword;
    @FXML
    private Label lblChangePassword;

    private Runnable onCloseCallback;

    private boolean isEditMode = false;
    private Worker selectedWorker;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setWorker(Worker selectedWorker) {
        this.selectedWorker = selectedWorker;

        if (selectedWorker != null) {
            isEditMode = true;
        }

        initItemsText();
        initButtons();
        initPasswordItems();
        if (isEditMode) {
            setWorkerData();
        }
        initCheckBox();
    }

    private void initCheckBox() {
        cbChangePassword.setOnAction(event -> {
            vbPassword.setVisible(cbChangePassword.isSelected());
            vbPasswordConfirm.setVisible(cbChangePassword.isSelected());
        });
    }

    private void initPasswordItems() {
        if (isEditMode) {
            hbPasswordCheck.setVisible(true);
            vbPassword.setVisible(false);
            vbPasswordConfirm.setVisible(false);
        } else {
            hbPasswordCheck.setVisible(false);
            hbPasswordCheck.setManaged(false);
            vbPassword.setVisible(true);
            vbPasswordConfirm.setVisible(true);
        }
    }

    private void initItemsText() {
        if (isEditMode) {
            lblTitle.setText(MessageHelper.getString("worker.form.lbl.title.update"));
        } else {
            lblTitle.setText(MessageHelper.getString("worker.form.lbl.title.create"));
        }

        lblChangePassword.setText(MessageHelper.getString("worker.form.lbl.change.password"));
        lblUserName.setText(MessageHelper.getString("worker.form.lbl.user.name"));
        lblPassword.setText(MessageHelper.getString("worker.form.lbl.password"));
        lblPasswordConfirm.setText(MessageHelper.getString("worker.form.lbl.password.confirm"));
        lblFirstName.setText(MessageHelper.getString("worker.form.lbl.first.name"));
        lblLastName.setText(MessageHelper.getString("worker.form.lbl.last.name"));
        lblEmail.setText(MessageHelper.getString("worker.form.lbl.email"));
        lblPhone.setText(MessageHelper.getString("worker.form.lbl.phone"));
    }

    private void initButtons() {
        if (isEditMode) {
            btnAction.setOnAction(e -> {
                updateWorker();
            });

            btnRefresh.setOnAction(e -> {
                setWorkerData();
            });
        } else {
            btnAction.setOnAction(e -> {
                createWorker();
            });

            btnRefresh.setOnAction(e -> {
                refreshData();
            });
        }
    }

    private void createWorker() {
        if (validateInputData()) {
            if (checkPassword()) {
                String username = tfUserName.getText();

                new Thread(() -> {
                    try {
                        boolean userExists = ApiService.getInstance().checkUserExist(username);

                        Platform.runLater(() -> {
                            if (userExists) {
                                DialogHelper.showAlert(
                                        MessageHelper.getString("msg.error"),
                                        MessageHelper.getString("worker.form.msg.user.exists"),
                                        AlertLevel.ERROR
                                );
                            } else {
                                User newWorker = new User();
                                newWorker.setUsername(username);
                                newWorker.setPassword(pfPassword.getText());
                                newWorker.setFirstName(tfFirstName.getText());
                                newWorker.setLastName(tfLastName.getText());
                                newWorker.setEmail(tfEmail.getText());
                                newWorker.setPhone(tfPhone.getText());

                                new Thread(() -> {
                                    try {
                                        Integer workerId = ApiService.getInstance().createWorker(newWorker);

                                        Platform.runLater(() -> {
                                            if (workerId != null) {
                                                DialogHelper.showAlert(
                                                        MessageHelper.getString("msg.updated"),
                                                        MessageHelper.getString("worker.form.msg.created"),
                                                        AlertLevel.INFO
                                                );
                                            } else {
                                                DialogHelper.showAlert(
                                                        MessageHelper.getString("msg.error"),
                                                        MessageHelper.getString("worker.form.msg.create.error"),
                                                        AlertLevel.ERROR
                                                );
                                            }

                                            if (onCloseCallback != null) {
                                                onCloseCallback.run();
                                            }
                                        });
                                    } catch (Exception e) {
                                        Platform.runLater(() -> DialogHelper.showAlert(
                                                MessageHelper.getString("msg.error"),
                                                MessageHelper.getString("worker.form.msg.create.error"),
                                                AlertLevel.ERROR
                                        ));
                                    }
                                }).start();
                            }
                        });

                    } catch (Exception e) {
                        log.error("Error user exist:", e);
                        Platform.runLater(() -> DialogHelper.showAlert(
                                MessageHelper.getString("msg.error"),
                                MessageHelper.getString("worker.form.msg.check.user.error"),
                                AlertLevel.ERROR
                        ));
                    }
                }).start();
            }
        }
    }

    private void updateWorker() {
        if (validateInputData()) {
            if (checkPassword()) {
                Worker worker = new Worker();
                worker.setId(selectedWorker.getId());

                User user = new User();
                user.setUsername(tfUserName.getText());
                user.setPassword(pfPassword.getText());
                user.setFirstName(tfFirstName.getText());
                user.setLastName(tfLastName.getText());
                user.setEmail(tfEmail.getText());
                user.setPhone(tfPhone.getText());
                worker.setUser(user);

                Boolean updatePassword = cbChangePassword.isSelected();

                Client client = CugomatFXApplication.getCurrentWorker().getClient();
                worker.setClient(client);

                new Thread(() -> {
                    try {
                        boolean updated = ApiService.getInstance().updateWorker(worker, updatePassword);

                        Platform.runLater(() -> {
                            if (updated) {
                                DialogHelper.showAlert(MessageHelper.getString("msg.updated"), MessageHelper.getString("worker.form.msg.updated"), AlertLevel.INFO);
                            } else {
                                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("worker.form.msg.update.error"), AlertLevel.ERROR);
                            }

                            if (onCloseCallback != null) {
                                onCloseCallback.run();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private boolean validateInputData() {
        boolean valid = true;

        if (tfUserName.getText().isEmpty()) {
            tfUserName.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfUserName.getStyleClass().remove("tf-error");
        }

        if (tfFirstName.getText().isEmpty()) {
            tfFirstName.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfFirstName.getStyleClass().remove("tf-error");
        }

        if (tfLastName.getText().isEmpty()) {
            tfLastName.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfLastName.getStyleClass().remove("tf-error");
        }

        if (!InputValidatorHelper.isEmailValid(tfEmail.getText())) {
            tfEmail.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfEmail.getStyleClass().remove("tf-error");
        }

        if (!InputValidatorHelper.isPhoneValid(tfPhone.getText())) {
            tfPhone.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfPhone.getStyleClass().remove("tf-error");
        }

        if (!isEditMode || (isEditMode && cbChangePassword.isSelected())) {
            if (pfPassword.getText().isEmpty()) {
                pfPassword.getStyleClass().add("tf-error");
                valid = false;
            } else {
                pfPassword.getStyleClass().remove("tf-error");
            }

            if (pfPasswordConfirm.getText().isEmpty()) {
                pfPasswordConfirm.getStyleClass().add("tf-error");
                valid = false;
            } else {
                pfPasswordConfirm.getStyleClass().remove("tf-error");
            }
        }

        return valid;
    }

    private boolean checkPassword() {
        String password = pfPassword.getText();
        String confirmPassword = pfPasswordConfirm.getText();

        if (!password.equals(confirmPassword)) {
            lblError.setText(MessageHelper.getString("worker.password.mismatch"));
            return false;
        } else {
            lblError.setText("");
            return true;
        }
    }

    private void refreshData() {
        lblUserName.setText("");
        lblPassword.setText("");
        lblPasswordConfirm.setText("");
        lblFirstName.setText("");
        lblLastName.setText("");
        lblEmail.setText("");
        lblPhone.setText("");
    }

    private void setWorkerData() {
        tfUserName.setText(selectedWorker.getUser().getUsername());
        tfFirstName.setText(selectedWorker.getUser().getFirstName());
        tfLastName.setText(selectedWorker.getUser().getLastName());
        tfEmail.setText(selectedWorker.getUser().getEmail());
        tfPhone.setText(selectedWorker.getUser().getPhone());
    }

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }
}
