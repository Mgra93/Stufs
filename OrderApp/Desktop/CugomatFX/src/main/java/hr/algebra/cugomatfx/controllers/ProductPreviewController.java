package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.helpers.Navigator;
import hr.algebra.cugomatfx.models.Category;
import hr.algebra.cugomatfx.models.Product;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductPreviewController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(ProductPreviewController.class);
    @FXML
    private Button btnCreateView;
    @FXML
    private Button btnUpdateView;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblCategory;
    @FXML
    private ComboBox<Category> cbCategory;
    @FXML
    private TableView<Product> tvProduct;
    @FXML
    private TableColumn<Product, String> colName;
    @FXML
    private TableColumn<Product, BigDecimal> colPrice;
    @FXML
    private TableColumn<Product, String> colCategory;
    @FXML
    private StackPane spContent;

    private Product selectedProduct;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setTexts();
        loadCategories();
        loadProducts(null);
        initButtons();
        setUpOrderTable();
    }

    private void setTexts() {
        btnCreateView.setText(MessageHelper.getString("product.preview.btn.create.view"));
        btnUpdateView.setText(MessageHelper.getString("product.preview.btn.update.view"));
        lblTitle.setText(MessageHelper.getString("product.preview.lbl.title"));
        btnDelete.setText(MessageHelper.getString("product.preview.btn.delete"));
        lblCategory.setText(MessageHelper.getString("product.preview.lbl.category"));
        colName.setText(MessageHelper.getString("product.preview.col.name"));
        colPrice.setText(MessageHelper.getString("product.preview.col.price"));
        colCategory.setText(MessageHelper.getString("product.preview.col.category"));
    }

    private void initButtons() {
        btnCreateView.setOnAction(e -> {
            ProductFormController controller = Navigator.loadPaneWithController(spContent, Navigator.PRODUCT_FORM_VIEW_PATH);

            if (controller != null) {
                controller.setProduct(null);
                controller.setOnCloseCallback(() -> {
                    loadProducts(null);
                    spContent.getChildren().clear();
                });
            }
        });

        btnUpdateView.setOnAction(e -> {
            if (selectedProduct != null) {
                ProductFormController controller = Navigator.loadPaneWithController(spContent, Navigator.PRODUCT_FORM_VIEW_PATH);

                if (controller != null) {
                    controller.setProduct(selectedProduct);
                    controller.setOnCloseCallback(() -> {
                        loadProducts(null);
                        spContent.getChildren().clear();
                    });
                }
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.preview.select"), AlertLevel.INFO);
            }
        });

        btnDelete.setOnAction(e -> {
            if (selectedProduct != null) {
                deleteProduct(selectedProduct);
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.preview.select"), AlertLevel.INFO);
            }
        });

        cbCategory.valueProperty().addListener((obs, oldCategory, newCategory) -> {
            if (newCategory != null) {
                loadProducts(newCategory.getId());
            }
        });
    }

    private void setUpOrderTable() {
        colName.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getName() != null ? o.getValue().getName() : "-"));
        colPrice.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getPrice() != null ? o.getValue().getPrice() : BigDecimal.ZERO));
        colCategory.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getCategory().getName() != null ? o.getValue().getCategory().getName() : "-"));

        colPrice.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.setScale(2, BigDecimal.ROUND_HALF_UP) + " " + MessageHelper.getString("currency.symbol"));
                }
            }
        });

        tvProduct.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selection) -> {
            selectedProduct = selection;
        });

        tvProduct.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadCategories() {
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

    private void loadProducts(Integer categoryId){
        new Thread(() -> {
            try {
                List<Product> productList = ApiService.getInstance().getProductList(categoryId);
                if (productList == null) productList = List.of();
                List<Product> finalProducts = productList;
                Platform.runLater(() -> {
                    tvProduct.getItems().clear();
                    tvProduct.getItems().setAll(finalProducts);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void deleteProduct(Product product) {
        new Thread(() -> {
            try {
                boolean deleted = ApiService.getInstance().deleteProduct(product.getId());
                Platform.runLater(() -> {
                    if (deleted) {
                        DialogHelper.showAlert(MessageHelper.getString("msg.deleted"), MessageHelper.getString("product.preview.deleted"), AlertLevel.INFO);
                        loadProducts(null);
                    } else {
                        DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.preview.deleted.error"), AlertLevel.ERROR);
                    }
                });
            } catch (Exception e) {
                log.error("Error deleting product:", e);
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("product.preview.deleted.error"), AlertLevel.ERROR);
            }
        }).start();
    }
}
