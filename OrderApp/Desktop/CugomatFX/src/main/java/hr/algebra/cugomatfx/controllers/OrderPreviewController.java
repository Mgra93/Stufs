package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.dto.OrderFilter;
import hr.algebra.cugomatfx.enums.OrderStatus;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.models.Order;
import hr.algebra.cugomatfx.models.Product;
import hr.algebra.cugomatfx.models.Worker;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OrderPreviewController implements Initializable {
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblProductList;
    @FXML
    private Label lblTotalPrice;
    @FXML
    private Label lblTotalPriceTxt;
    @FXML
    private DatePicker dpDate;
    @FXML
    private Label lblWorker;
    @FXML
    private ComboBox<Worker> cbWorker;
    @FXML
    private Label lblStatus;
    @FXML
    private ComboBox<OrderStatus> cbStatus;
    @FXML
    private Button btnRefresh;
    @FXML
    private TableView<Order> tvOrders;
    @FXML
    private TableColumn<Order, String> colTableCode;
    @FXML
    private TableColumn<Order, String> colCreatedOn;
    @FXML
    private TableColumn<Order, BigDecimal> colTotalPrice;
    @FXML
    private TableColumn<Order, Boolean> colHasDiscount;
    @FXML
    private TableColumn<Order, BigDecimal> colFinalPrice;
    @FXML
    private TableColumn<Order, String> colUser;
    @FXML
    private TableColumn<Order, String> colWorker;
    @FXML
    private TableColumn<Order, String> colStatus;
    @FXML
    private TableView<Product> tvProducts;
    @FXML
    private TableColumn<Product, String> colProductCategory;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, BigDecimal> colProductPrice;
    @FXML
    private TableColumn<Product, Integer> colProductQuantity;
    @FXML
    private Label lblUser;
    @FXML
    private TextField tfUser;

    private PauseTransition userFilterPause;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumns();
        setTableEvents();
        loadOrders();
        getWorkers();
        setItemsText();
        initFilterElements();
    }

    private void initFilterElements() {
        cbStatus.getItems().clear();
        cbStatus.getItems().addAll(OrderStatus.values());

        cbStatus.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadOrders();
            }
        });

        cbWorker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadOrders();
            }
        });

        dpDate.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                loadOrders();
            }
        });

        btnRefresh.setOnAction(event -> {
            dpDate.setValue(null);
            cbWorker.valueProperty().setValue(null);
            cbStatus.valueProperty().setValue(null);
            cbStatus.setPromptText(MessageHelper.getString("order.preview.cb.status.prompt"));
            cbWorker.setPromptText(MessageHelper.getString("order.preview.cb.worker.prompt"));
            loadOrders();
        });

        userFilterPause = new PauseTransition(Duration.seconds(2));
        tfUser.textProperty().addListener((obs, oldText, newText) -> {
            userFilterPause.stop();
            userFilterPause.setOnFinished(event -> loadOrders());
            userFilterPause.playFromStart();
        });
    }

    private void setTableEvents() {
        tvOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selection) -> {
            if (selection != null) {
                tvProducts.getItems().clear();
                if (selection.getProducts() != null) {
                    tvProducts.setItems(FXCollections.observableArrayList(selection.getProducts()));
                    lblTotalPrice.setText(selection.getTotalPrice().toString() + MessageHelper.getString("currency.symbol"));
                }

                lblTotalPrice.visibleProperty().setValue(true);
                lblTotalPriceTxt.visibleProperty().setValue(true);
            } else {
                lblTotalPrice.visibleProperty().setValue(false);
                lblTotalPriceTxt.visibleProperty().setValue(false);
            }
        });
    }

    private void setItemsText() {
        lblTitle.setText(MessageHelper.getString("order.preview.lbl.title"));
        lblDate.setText(MessageHelper.getString("order.preview.lbl.date"));
        lblWorker.setText(MessageHelper.getString("order.preview.lbl.worker"));
        lblStatus.setText(MessageHelper.getString("order.preview.lbl.status"));
        lblProductList.setText(MessageHelper.getString("order.preview.lbl.product.list"));
        lblTotalPriceTxt.setText(MessageHelper.getString("order.preview.lbl.total.price"));
        lblUser.setText(MessageHelper.getString("order.preview.lbl.user"));

        colTableCode.setText(MessageHelper.getString("order.preview.col.table.code"));
        colCreatedOn.setText(MessageHelper.getString("order.preview.col.table.created.on"));
        colTotalPrice.setText(MessageHelper.getString("order.preview.col.total.price"));
        colHasDiscount.setText(MessageHelper.getString("order.preview.col.total.discount"));
        colFinalPrice.setText(MessageHelper.getString("order.preview.col.final.price"));
        colUser.setText(MessageHelper.getString("order.preview.col.user"));
        colWorker.setText(MessageHelper.getString("order.preview.col.worker"));
        colStatus.setText(MessageHelper.getString("order.preview.col.status"));

        colProductCategory.setText(MessageHelper.getString("order.preview.col.category"));
        colProductName.setText(MessageHelper.getString("order.preview.col.name"));
        colProductPrice.setText(MessageHelper.getString("order.preview.col.price"));
        colProductQuantity.setText(MessageHelper.getString("order.preview.col.quantity"));

        cbStatus.setPromptText(MessageHelper.getString("order.preview.cb.status.prompt"));
        cbWorker.setPromptText(MessageHelper.getString("order.preview.cb.worker.prompt"));

        tvProducts.setPlaceholder(new Label(MessageHelper.getString("order.preview.prom.table.product")));
        tvOrders.setPlaceholder(new Label(MessageHelper.getString("order.preview.prom.table.order")));
    }

    public void setColumns() {
        colTableCode.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getTableCode() != null ? o.getValue().getTableCode() : "-"));
        colCreatedOn.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getCreatedOn() != null ? o.getValue().getCreatedOn().format(DATE_FORMATTER) : "-"));
        colTotalPrice.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getTotalPrice() != null ? o.getValue().getTotalPrice() : BigDecimal.ZERO));
        colFinalPrice.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getFinalPrice() != null ? o.getValue().getFinalPrice() : BigDecimal.ZERO));
        colHasDiscount.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getHasDiscount() != null ? o.getValue().getHasDiscount() : false));
        colUser.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getUser() != null ? o.getValue().getUser().getFullName() : "-"));
        colWorker.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getWorker() != null ? o.getValue().getWorker().getFullName() : "-"));
        colStatus.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getStatus() != null && OrderStatus.fromCode(o.getValue().getStatus()) != null ? OrderStatus.fromCode(o.getValue().getStatus()).toString() : "-"));
        colProductCategory.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getCategory() != null ? p.getValue().getCategory().getName() : "-"));
        colProductName.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getName() != null ? p.getValue().getName() : "-"));
        colProductPrice.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getPrice() != null ? p.getValue().getPrice() : BigDecimal.ZERO));
        colProductQuantity.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getQuantity() != null ? p.getValue().getQuantity() : 0));


        colTotalPrice.setCellFactory(column -> new TableCell<>() {
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

        colFinalPrice.setCellFactory(column -> new TableCell<>() {
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

        colProductPrice.setCellFactory(column -> new TableCell<>() {
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

        colHasDiscount.setCellFactory(column -> new TableCell<>() {
            private final FontIcon icon = new FontIcon("fas-circle");

            {
                icon.setIconSize(14);
            }

            @Override
            protected void updateItem(Boolean hasDiscount, boolean empty) {
                super.updateItem(hasDiscount, empty);

                if (empty || hasDiscount == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    icon.getStyleClass().removeAll("icon-status-active", "icon-status-inactive");

                    if (hasDiscount) {
                        icon.getStyleClass().add("icon-status-active");
                    } else {
                        icon.getStyleClass().add("icon-status-inactive");
                    }

                    setText(null);
                    setGraphic(icon);
                }
            }
        });



        tvOrders.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvProducts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadOrders() {
        OrderFilter filter = new OrderFilter();

        if (cbWorker.getSelectionModel().getSelectedItem() != null) {
            filter.setWorker(cbWorker.getSelectionModel().getSelectedItem().getUser().getUsername());
        }

        if (dpDate.getValue() != null) {
            filter.setDate(dpDate.getValue());
        }

        if (cbStatus.getValue() != null) {
            filter.setStatus(cbStatus.getValue().getCode());
        }

        if(!tfUser.getText().isEmpty()){
            filter.setUser(tfUser.getText());
        }

        getOrders(filter);
    }

    private void getOrders(OrderFilter filter) {
        new Thread(() -> {
            try {
                List<Order> orders = ApiService.getInstance().getOrdersByFilter(filter);
                Platform.runLater(() -> {
                    tvOrders.getItems().clear();
                    tvOrders.setItems(FXCollections.observableArrayList(orders));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void getWorkers() {
        new Thread(() -> {
            try {
                List<Worker> workerList = ApiService.getInstance().getWorkerList();
                Platform.runLater(() -> {
                    cbWorker.getItems().clear();
                    cbWorker.setItems(FXCollections.observableArrayList(workerList));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
