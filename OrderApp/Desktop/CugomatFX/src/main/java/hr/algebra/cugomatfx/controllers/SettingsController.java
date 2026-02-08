package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.enums.DayOfWeek;
import hr.algebra.cugomatfx.enums.UserRole;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.models.Client;
import hr.algebra.cugomatfx.models.Worker;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    private Label lblUserInfo;
    @FXML
    private Label lblClientInfo;
    @FXML
    private Label lblUserFullName;
    @FXML
    private Label lblUserEmail;
    @FXML
    private Label lblUserPhone;
    @FXML
    private Label lblUserUsername;
    @FXML
    private Label lblClientName;
    @FXML
    private Label lblClientCode;
    @FXML
    private Label lblClientAddress;
    @FXML
    private Label lblClientPhone;
    @FXML
    private Label lblClientOib;
    @FXML
    private Label lblClientStatus;
    @FXML
    private FontIcon fiStatus;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDateLicence;
    @FXML
    private TextField tfLocationSecret;
    @FXML
    private CheckBox cbVipDayActive;
    @FXML
    private ComboBox<DayOfWeek> cbVipDayCode;
    @FXML
    private VBox vbLicence;
    @FXML
    private Label lblLicenceInfo;
    @FXML
    private Label lblClientWeb;
    @FXML
    private Button btnUpdate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setItemsText();
        initTextField();
        initDayComboBox();
        initButtons();
        showData();
        setVisibleByRole();
    }

    private void initTextField() {
        btnUpdate.setDisable(tfLocationSecret.getText().isEmpty());

        tfLocationSecret.textProperty().addListener((observable, oldValue, newValue) -> {
            btnUpdate.setDisable(newValue.isEmpty());
        });
    }

    private void initButtons() {
        btnUpdate.setOnAction(_ -> {
            updateClient();
        });
    }

    private void setVisibleByRole() {
        UserRole role = UserRole.fromString(ApiService.getInstance().getRole());

        if (role == UserRole.ROLE_ADMIN) {
            vbLicence.setVisible(true);
            btnUpdate.setOnAction(_ -> {
                updateClient();
            });
        }
    }

    private void updateClient() {
        Client updatedClient = new Client();

        updatedClient.setCode(CugomatFXApplication.getCurrentWorker().getClient().getCode());
        if (!tfLocationSecret.getText().isEmpty()) {
            updatedClient.setLocationSecret(tfLocationSecret.getText());
        }
        updatedClient.setVipDayActive(cbVipDayActive.isSelected());
        if (cbVipDayCode.getSelectionModel().getSelectedItem() != null) {
            updatedClient.setVipDayCode(cbVipDayCode.getSelectionModel().getSelectedItem().getCode());
        }
        try {
            Boolean updated = ApiService.getInstance().updateClient(updatedClient);

            if (updated != null && updated) {
                DialogHelper.showAlert(MessageHelper.getString("msg.updated"), MessageHelper.getString("setting.lbl.updated"), AlertLevel.INFO);

                try {
                    Client newClient = ApiService.getInstance().getClient(CugomatFXApplication.getCurrentWorker().getClient().getCode());
                    CugomatFXApplication.getCurrentWorker().setClient(newClient);
                    showData();
                } catch (Exception e) {
                    DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("setting.lbl.error"), AlertLevel.ERROR);
                }
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("setting.lbl.error"), AlertLevel.ERROR);
            }
        } catch (Exception e) {
            DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("setting.lbl.error"), AlertLevel.ERROR);
        }
    }

    private void initDayComboBox() {
        cbVipDayCode.getItems().addAll(DayOfWeek.values());
    }

    private void setItemsText() {
        lblUserInfo.setText(MessageHelper.getString("settings.lbl.user.info"));
        lblClientInfo.setText(MessageHelper.getString("setting.lbl.client.name"));
        lblTitle.setText(MessageHelper.getString("setting.lbl.title"));
        lblLicenceInfo.setText(MessageHelper.getString("setting.lbl.licence"));
    }

    private void showData() {
        String fullName = "";
        Worker worker = CugomatFXApplication.getCurrentWorker();

        if (worker != null) {
            if (worker.getUser() != null) {
                if (worker.getUser().getFirstName() != null) {
                    fullName += worker.getUser().getFirstName() + " ";
                }

                if (worker.getUser().getLastName() != null) {
                    fullName += worker.getUser().getLastName();
                }

                lblUserFullName.setText(fullName);

                if (worker.getUser().getUsername() != null) {
                    lblUserUsername.setText(worker.getUser().getUsername());
                }

                if (worker.getUser().getEmail() != null) {
                    lblUserEmail.setText(worker.getUser().getEmail());
                }

                if (worker.getUser().getPhone() != null) {
                    lblUserPhone.setText(worker.getUser().getPhone());
                }
            }

            if (worker.getClient() != null) {
                if (worker.getClient().getName() != null) {
                    lblClientName.setText(worker.getClient().getName());
                }

                if (worker.getClient().getName() != null) {
                    lblClientName.setText(worker.getClient().getName());
                }

                if (worker.getClient().getCode() != null) {
                    lblClientCode.setText(worker.getClient().getCode());
                }

                if (worker.getClient().getAddress() != null) {
                    lblClientAddress.setText(worker.getClient().getAddress());
                }

                if (worker.getClient().getPhone() != null) {
                    lblClientPhone.setText(worker.getClient().getPhone());
                }

                if (worker.getClient().getOib() != null) {
                    lblClientOib.setText(worker.getClient().getOib());
                }

                if (worker.getClient().getOib() != null) {
                    lblClientOib.setText(worker.getClient().getOib());
                }

                if (worker.getClient().getOib() != null) {
                    lblClientOib.setText(worker.getClient().getOib());
                }

                if (worker.getClient().getWebPage() != null) {
                    lblClientWeb.setText(worker.getClient().getWebPage());
                }

                if (worker.getClient().getActive() != null) {
                    fiStatus.getStyleClass().removeAll("icon-status-active", "icon-status-inactive");

                    if (worker.getClient().getActive()) {
                        lblClientStatus.setText(MessageHelper.getString("setting.txt.active"));
                        fiStatus.getStyleClass().add("icon-status-active");
                    } else {
                        lblClientStatus.setText(MessageHelper.getString("setting.txt.inactive"));
                        fiStatus.getStyleClass().add("icon-status-inactive");
                    }
                }

                if (worker.getClient().getLicenseExpiryTime() != null) {
                    lblDateLicence.setText(worker.getClient().getLicenseExpiryTime().format(DATE_FORMATTER));
                }

                if (worker.getClient().getLocationSecret() != null) {
                    tfLocationSecret.setText(worker.getClient().getLocationSecret());
                }

                if (worker.getClient().getVipDayActive() != null) {
                    cbVipDayActive.setSelected(worker.getClient().getVipDayActive());

                    if (worker.getClient().getVipDayActive()) {
                        cbVipDayCode.getSelectionModel().select(DayOfWeek.fromCode(worker.getClient().getVipDayCode()));
                    }
                }
            }
        }
    }
}
