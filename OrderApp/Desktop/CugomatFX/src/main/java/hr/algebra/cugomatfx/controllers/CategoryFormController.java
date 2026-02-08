package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.models.Category;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryFormController implements Initializable {
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblName;
    @FXML
    private TextField tfName;
    @FXML
    private Button btnAction;
    @FXML
    private Button btnRefresh;

    private Runnable onCloseCallback;

    private boolean isEditMode = false;
    private Category selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    public void setCategory(Category category) {
        this.selectedCategory = category;

        if (selectedCategory != null) {
            isEditMode = true;
        }

        initItemsText();
        initButtons();
        if (isEditMode) {
            setCategoryData();
        }
    }

    private void initItemsText() {
        if (isEditMode) {
            lblTitle.setText(MessageHelper.getString("category.form.lbl.title.update"));
        } else {
            lblTitle.setText(MessageHelper.getString("category.form.lbl.title.create"));
        }

        lblName.setText(MessageHelper.getString("category.form.lbl.name"));
    }

    private void initButtons() {
        if (isEditMode) {
            btnAction.setOnAction(e -> {
                updateCategory();
            });

            btnRefresh.setOnAction(e -> {
                setCategoryData();
            });
        } else {
            btnAction.setOnAction(e -> {
                createCategory();
            });

            btnRefresh.setOnAction(e -> {
                refreshData();
            });
        }
    }

    private void createCategory() {
        if (validateInputData()) {
            Category category = new Category();
            category.setName(tfName.getText());

            new Thread(() -> {
                try {
                    Integer categoryId = ApiService.getInstance().createCategory(category);

                    Platform.runLater(() -> {
                        if (categoryId != null) {
                            DialogHelper.showAlert(MessageHelper.getString("msg.updated"), MessageHelper.getString("category.alert.create"), AlertLevel.INFO);
                        } else {
                            DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("category.alert.error.create"), AlertLevel.ERROR);
                        }

                        if (onCloseCallback != null) {
                            onCloseCallback.run();
                        }
                    });
                } catch (Exception e) {
                    DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("category.alert.error.create"), AlertLevel.ERROR);
                    System.out.println(e);
                }
            }).start();
        }
    }

    private void updateCategory() {
        if (validateInputData()) {
            Category category = new Category();
            category.setId(selectedCategory.getId());
            category.setName(tfName.getText());
            new Thread(() -> {
                try {
                    boolean updated = ApiService.getInstance().updateCategory(category);

                    Platform.runLater(() -> {
                        if (updated) {
                            DialogHelper.showAlert(MessageHelper.getString("msg.updated"), MessageHelper.getString("category.alert.update"), AlertLevel.INFO);
                        } else {
                            DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("category.alert.error.update"), AlertLevel.ERROR);
                        }

                        if (onCloseCallback != null) {
                            onCloseCallback.run();
                        }
                    });
                } catch (Exception e) {
                    DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("category.alert.error.update"), AlertLevel.ERROR);
                }
            }).start();
        }
    }

    private boolean validateInputData() {
        if (tfName.getText().isEmpty()) {
            tfName.getStyleClass().add("tf-error");
            return false;
        } else {
            tfName.getStyleClass().remove("tf-error");
        }

        return true;
    }


    private void setCategoryData() {
        tfName.setText(selectedCategory.getName());
    }


    private void refreshData() {
        tfName.setText("");
    }

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }
}
