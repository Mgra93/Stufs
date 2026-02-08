package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.models.*;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductFormController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(ProductFormController.class);
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfPrice;
    @FXML
    private ComboBox<Category> cbCategory;
    @FXML
    private Button btnAction;
    @FXML
    private Button btnRefresh;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblName;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblCategory;

    private Runnable onCloseCallback;

    private boolean isEditMode = false;
    private Product selectedProduct;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setProduct(Product product) {
        this.selectedProduct = product;

        if (selectedProduct != null) {
            isEditMode = true;
            setProductData(selectedProduct);
        }

        setItemsText();
        getCategory();
        initButtons();
        initPriceField();
    }

    private void initPriceField() {
        tfPrice.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            } else {
                return null;
            }
        }));
    }

    private void initButtons() {
        if (isEditMode) {
            btnAction.setOnAction(event -> {
                updateProduct();
            });

            btnRefresh.setOnAction(event -> {
                setProductData(selectedProduct);
            });
        } else {
            btnAction.setOnAction(event -> {
                createProduct();
            });

            btnRefresh.setOnAction(event -> {
                refreshInputs();
            });
        }
    }


    private void refreshInputs() {
        cbCategory.getSelectionModel().clearSelection();
        tfName.setText("");
        tfPrice.setText("");
    }

    private void setItemsText() {
        if (isEditMode) {
            lblTitle.setText(MessageHelper.getString("product.form.lbl.title.update"));
        } else {
            lblTitle.setText(MessageHelper.getString("product.form.lbl.title.create"));
        }

        lblCategory.setText(MessageHelper.getString("product.form.lbl.category"));
        lblName.setText(MessageHelper.getString("product.form.lbl.name"));
        lblPrice.setText(MessageHelper.getString("product.form.lbl.price"));
        cbCategory.setPromptText(MessageHelper.getString("product.form.cb.category.prompt"));
    }

    private void getCategory() {
        new Thread(() -> {
            try {
                List<Category> categories = ApiService.getInstance().getCategoryList();
                if (categories == null) categories = List.of();
                List<Category> finalCategories = categories;
                Platform.runLater(() -> {
                    cbCategory.getItems().clear();
                    cbCategory.getItems().setAll(finalCategories);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void createProduct() {
        if (validateInputData()) {
            Product newProduct = new Product();

            newProduct.setName(tfName.getText());
            newProduct.setPrice(tfPrice.getText().isEmpty() ? BigDecimal.ZERO : new BigDecimal(tfPrice.getText()));
            newProduct.setCategory(cbCategory.getValue());

            new Thread(() -> {
                try {
                    Integer createdId = ApiService.getInstance().createProduct(newProduct);
                    Platform.runLater(() -> {
                        if (createdId != null) {
                            DialogHelper.showAlert(MessageHelper.getString("msg.created"), MessageHelper.getString("product.form.msg.created"), AlertLevel.INFO);
                        } else {
                            DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.form.msg.error"), AlertLevel.ERROR);
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

    private boolean validateInputData() {
        Boolean valid = true;

        if (tfName.getText().isEmpty()) {
            tfName.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfName.getStyleClass().remove("tf-error");
        }

        if (tfPrice.getText().isEmpty()) {
            tfPrice.getStyleClass().add("tf-error");
            valid = false;
        } else {
            tfPrice.getStyleClass().remove("tf-error");
        }

        if (cbCategory.getSelectionModel().getSelectedItem() == null) {
            cbCategory.getStyleClass().add("tf-error");
            valid = false;
        } else {
            cbCategory.getStyleClass().remove("tf-error");
        }

        return valid;
    }

    private void updateProduct() {
        if (validateInputData()) {

            Product updatedProduct = new Product();
            updatedProduct.setId(selectedProduct.getId());
            updatedProduct.setName(tfName.getText());
            updatedProduct.setCategory(cbCategory.getValue());
            updatedProduct.setPrice(tfPrice.getText().isEmpty() ? null : new BigDecimal(tfPrice.getText()));

            new Thread(() -> {
                try {
                    boolean updated = ApiService.getInstance().updateProduct(updatedProduct);

                    Platform.runLater(() -> {
                        if (updated) {
                            DialogHelper.showAlert(MessageHelper.getString("msg.updated"), MessageHelper.getString("product.form.msg.updated"), AlertLevel.INFO);
                        } else {
                            DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.form.msg.updated.error"), AlertLevel.ERROR);
                        }

                        if (onCloseCallback != null) {
                            onCloseCallback.run();
                        }
                    });
                } catch (Exception e) {
                    log.error("Error updating product:", e);
                    DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.form.msg.updated.error"), AlertLevel.ERROR);
                }
            }).start();
        }

    }

    public void setProductData(Product product) {
        tfName.setText(product.getName());
        tfPrice.setText(product.getPrice().toString());
        cbCategory.setValue(product.getCategory());
    }

    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }
}
