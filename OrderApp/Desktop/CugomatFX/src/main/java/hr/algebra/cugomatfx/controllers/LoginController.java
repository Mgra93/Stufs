package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.dto.AuthRequestDTO;
import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.enums.Language;
import hr.algebra.cugomatfx.helpers.*;
import hr.algebra.cugomatfx.models.AccessData;
import hr.algebra.cugomatfx.models.Category;
import hr.algebra.cugomatfx.models.JwtResponse;
import hr.algebra.cugomatfx.models.Worker;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable  {
    @FXML
    private TextField tfUserName;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Button btnLogIn;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblPassword;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblError;
    @FXML
    private Label lblLanguage;
    @FXML
    private Hyperlink hlPassword;
    @FXML
    private ComboBox<Language> cbLanguage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setText();
        initButtons();
        initComboLanguage();
        if(certCheck()){
            checkServer();
        };
    }

    private void initComboLanguage() {
        cbLanguage.getItems().setAll(Language.values());
        cbLanguage.getSelectionModel().select(Language.HR);

        cbLanguage.valueProperty().addListener((obs, oldLang, newLang) -> {
            if (newLang != null) {
                changeLanguage(newLang);
            }
        });
    }

    private void changeLanguage(Language language) {
        MessageHelper.changeLanguage(language.getCode());
        setText();
    }

    private void initButtons(){
        btnLogIn.setDefaultButton(true);
        btnLogIn.setOnAction(event -> login());
    }

    private void login() {
        String username = tfUserName.getText().trim();
        String password = pfPassword.getText();

        if (username.isEmpty()) {
            lblError.setText(MessageHelper.getString("error.missing.username"));
            tfUserName.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            lblError.setText(MessageHelper.getString("error.missing.password"));
            pfPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            lblError.setText(MessageHelper.getString("error.size.password"));
            pfPassword.requestFocus();
            return;
        }

        lblError.setText("");

        AuthRequestDTO requestDTO = new AuthRequestDTO(username, password);

        try {
            JwtResponse response = ApiService.getInstance().postLogin(requestDTO);

            if (response.getErrorMessage() != null && !response.getErrorMessage().isEmpty()) {
                lblError.setText(response.getErrorMessage());
                return;
            }

            AccessData accessData = JwtHelper.extractAccessData(response);
            RoleChecker checker = new RoleChecker(accessData.getAccessToken());
            if (checker.isAdmin() || checker.isWorker()) {
                setAccessData(accessData);
                setWorkerData(accessData.getUser());
                Navigator.loadScene(Navigator.MAIN_VIEW_PATH);
            } else {
                lblError.setText(MessageHelper.getString("error.user.role"));
            }

            System.out.println("User: " + accessData.getUser());
            System.out.println("Role: " + accessData.getRole());
        } catch (Exception e) {
            lblError.setText(MessageHelper.getString("login.login.error"));
        }
    }

    private void setWorkerData(String user) {
        try{
            Worker worker = ApiService.getInstance().getWorker(user);
            CugomatFXApplication.setCurrentWorker(worker);
        }catch (Exception e){
            lblError.setText(MessageHelper.getString("error.worker.dont.exist"));
            e.printStackTrace();
        }
    }

    private void setAccessData(AccessData accessData){
        ApiService.getInstance().setAccessData(accessData);
    }

    private void setText(){
        lblUserName.setText(MessageHelper.getString("login.lbl.username"));
        lblPassword.setText(MessageHelper.getString("login.lbl.password"));
        lblLanguage.setText(MessageHelper.getString("login.lbl.language"));
        btnLogIn.setText(MessageHelper.getString("login.btn.login"));
        lblTitle.setText(MessageHelper.getString("login.app.title"));
        hlPassword.setText(MessageHelper.getString("login.hl.password"));
        tfUserName.setPromptText(MessageHelper.getString("login.tf.username.prompt"));
        pfPassword.setPromptText(MessageHelper.getString("login.tf.password.prompt"));
    }

    private void checkServer() {
        try {
            Boolean serverActive = ApiService.getInstance().pingServer();
            if(!serverActive){
                lblError.setText(MessageHelper.getString("error.api.no.available"));
                btnLogIn.setDisable(true);
            }
        } catch (Exception e) {
            lblError.setText(MessageHelper.getString("error.api.no.available"));
            btnLogIn.setDisable(true);
        }
    }

    private boolean certCheck() {
        boolean certAvailable = CertHelper.isCertificateAvailable();

        if (!certAvailable) {
            disableApp(MessageHelper.getString("login.cert.missing"));
        }

        return certAvailable;
    }

    private void disableApp(String msg){
        lblError.setText(msg);
        btnLogIn.setDisable(true);
    }
}