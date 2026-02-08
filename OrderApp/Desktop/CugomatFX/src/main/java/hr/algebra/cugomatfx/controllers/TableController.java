package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.CugomatFXApplication;
import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.enums.OrderStatus;
import hr.algebra.cugomatfx.enums.TablePrefix;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.helpers.Navigator;
import hr.algebra.cugomatfx.models.BarcodeData;
import hr.algebra.cugomatfx.models.Order;
import hr.algebra.cugomatfx.models.Product;
import hr.algebra.cugomatfx.models.StickerGenerator;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.application.Platform;
import org.kordamp.ikonli.javafx.FontIcon;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TableController implements Initializable {
    @FXML
    private GridPane gpInside;
    @FXML
    private GridPane gpOutside;
    @FXML
    private GridPane gpBarOne;
    @FXML
    private GridPane gpBarTwo;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblActiveOrders;
    @FXML
    private Label lblOrderCounter;
    @FXML
    private Label lblDrinks;
    @FXML
    private Label lblIndoor;
    @FXML
    private Label lblOutdoor;
    @FXML
    private Label lblBarOne;
    @FXML
    private Label lblBarTwo;
    @FXML
    private TableView<Order> tvOrders;
    @FXML
    private TableColumn<Order, String> colTable;
    @FXML
    private TableColumn<Order, Integer> colItems;
    @FXML
    private TableColumn<Order, String> colCreated;
    @FXML
    private TableColumn<Order, String> colUser;
    @FXML
    private Label lblTableCodeTxt;
    @FXML
    private Label lblCreatedOnTxt;
    @FXML
    private Label lblUserNameTxt;
    @FXML
    private Label lblTotalPriceTxt;
    @FXML
    private Label lblDiscountTxt;
    @FXML
    private Label lblFinalPriceTxt;
    @FXML
    private Label lblTotalPrice;
    @FXML
    private Label lblTableCode;
    @FXML
    private Label lblCreatedOn;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblProduct;
    @FXML
    private Label lblOrders;
    @FXML
    private FontIcon fiDiscount;
    @FXML
    private Label lblFinalPrice;
    @FXML
    private ListView<String> lvProducts;
    @FXML
    private TableView tvProducts;
    @FXML
    private TableColumn<Product, String> colProductName;
    @FXML
    private TableColumn<Product, BigDecimal> colProductPrice;
    @FXML
    private TableColumn<Product, Integer> colProductQuantity;
    @FXML
    private TableColumn<Product, BigDecimal> colTotalProductPrice;
    @FXML
    private Button btnSticker;
    @FXML
    private Button btnComplete;
    @FXML
    private Button btnReject;
    @FXML
    private GridPane gpOrderData;


    private static final int INSIDE_ROWS = 4;
    private static final int INSIDE_COLUMNS = 4;
    private static final int OUTSIDE_ROWS = 4;
    private static final int OUTSIDE_COL = 3;
    private static final int BAR_ONE_ROWS = 1;
    private static final int BAR_ONE_COLS = 4;
    private static final int BAR_TWO_ROWS = 1;
    private static final int BAR_TWO_COLS = 4;
    private static final int IMG_VIEW_WIDTH = 100;
    private static final int IMG_VIEW_HEIGHT = 100;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Map<String, Button> tableButtons = new HashMap<>();
    private Map<String, List<Order>> ordersByTable = new HashMap<>();
    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createArea(gpInside, TablePrefix.INSIDE, INSIDE_ROWS, INSIDE_COLUMNS);
        createArea(gpOutside, TablePrefix.OUTSIDE, OUTSIDE_ROWS, OUTSIDE_COL);
        createArea(gpBarOne, TablePrefix.BAR_ONE, BAR_ONE_ROWS, BAR_ONE_COLS);
        createArea(gpBarTwo, TablePrefix.BAR_TWO, BAR_TWO_ROWS, BAR_TWO_COLS);

        setItemsText();
        setUpOrderTable();
        setUpProductTable();
        initButtons();
        start();
    }

    private void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            getOrders();
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void initButtons() {
        btnSticker.setOnAction(actionEvent -> {
            generateTableStickerDialog();
        });

        btnComplete.setOnAction(actionEvent -> {
            changeOrderStatus(OrderStatus.COMPLETED);
        });

        btnReject.setOnAction(actionEvent -> {
            changeOrderStatus(OrderStatus.REJECTED);
        });
    }

    private void createArea(GridPane gridPane, TablePrefix prefix, int rows, int cols) {
        Image insideTableImg = new Image(getClass().getResourceAsStream(Navigator.INSIDE_TABLE_IMG_PATH));
        Image outsideTableImg = new Image(getClass().getResourceAsStream(Navigator.OUTSIDE_TABLE_IMG_PATH));
        Image barStoolImg = new Image(getClass().getResourceAsStream(Navigator.BAR_TABLE_IMG_PATH));

        int counter = 1;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button btn = new Button();
                btn.getStyleClass().add("btn-card");
                String tableCode = prefix.getCode() + counter;
                btn.setId("btn" + tableCode);

                StackPane stack = new StackPane();
                Image tableImg;
                switch (prefix) {
                    case INSIDE -> tableImg = insideTableImg;
                    case OUTSIDE -> tableImg = outsideTableImg;
                    case BAR_ONE, BAR_TWO -> tableImg = barStoolImg;
                    default -> tableImg = insideTableImg;
                }

                ImageView imgView = new ImageView(tableImg);
                imgView.setFitWidth(IMG_VIEW_WIDTH);
                imgView.setFitHeight(IMG_VIEW_HEIGHT);
                imgView.setPreserveRatio(true);

                Label lbl = new Label(tableCode);
                lbl.getStyleClass().add("btnAreaText");
                stack.getChildren().addAll(imgView, lbl);

                btn.setGraphic(stack);
                gridPane.add(btn, col, row);

                tableButtons.put(tableCode, btn);
                btn.setOnAction(e -> tableOrderCheck(tableCode));

                counter++;
            }
        }
    }

    private void tableOrderCheck(String tableCode) {
        List<Order> list = ordersByTable.get(tableCode);
        if (list == null || list.isEmpty()) return;

        if (list.size() == 1) {
            tvOrders.getSelectionModel().select(list.get(0));
            showOrderData(list.get(0));
        } else {
            DialogHelper.showChoiceDialog(MessageHelper.getString("table.dialog.title"),
                            MessageHelper.getString("table.dialog.table") + tableCode +
                                    MessageHelper.getString("table.dialog.orders"), MessageHelper.getString("table.dialog.table"), list)
                    .ifPresent(order -> {
                        tvOrders.getSelectionModel().select(order);
                        showOrderData(order);
                    });
        }
    }

    private void getOrders() {
        new Thread(() -> {
            try {
                List<Order> orderList = ApiService.getInstance().getActiveOrders();
                orderList = (orderList == null) ? new ArrayList<>() : orderList;

                List<Order> finalOrderList = orderList;

                Platform.runLater(() -> {
                    setActiveProducts(finalOrderList);
                    setOrderCount(finalOrderList.size());
                    tvOrders.setItems(FXCollections.observableArrayList(finalOrderList));
                    setTablesOrderStyle(finalOrderList);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void changeOrderStatus(OrderStatus orderStatus) {
        if (tvOrders.getSelectionModel().getSelectedItem() != null) {
            new Thread(() -> {
                try {
                    Order selectedOrder = tvOrders.getSelectionModel().getSelectedItem();
                    boolean statusChanged = ApiService.getInstance().setOrderStatus(selectedOrder, orderStatus);

                    if (statusChanged) {
                        DialogHelper.showAlert(MessageHelper.getString("msg.statusChanged"), "Odabrana naruđba izvršena.", AlertLevel.INFO);
                        getOrders();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void setTablesOrderStyle(List<Order> orders) {
        ordersByTable.clear();
        for (Button btn : tableButtons.values()) {
            StackPane stack = (StackPane) btn.getGraphic();
            if (stack != null && !stack.getChildren().isEmpty() && stack.getChildren().get(1) instanceof Label) {
                Label lbl = (Label) stack.getChildren().get(1);
                lbl.getStyleClass().removeAll("btn-text-no-order", "btn-text-order");
                lbl.getStyleClass().add("btn-text-no-order");
            }
        }

        for (Order o : orders) {
            if (o.getTableCode() != null) {
                ordersByTable.computeIfAbsent(o.getTableCode(), k -> new ArrayList<>()).add(o);
            }
        }

        for (String tableCode : ordersByTable.keySet()) {
            Button btn = tableButtons.get(tableCode);
            if (btn != null) {
                StackPane stack = (StackPane) btn.getGraphic();
                if (stack != null && !stack.getChildren().isEmpty() && stack.getChildren().get(1) instanceof Label) {
                    Label lbl = (Label) stack.getChildren().get(1);
                    lbl.getStyleClass().removeAll("btn-text-no-order", "btn-text-order");
                    lbl.getStyleClass().add("btn-text-order");
                }
            }
        }
    }

    private void setItemsText() {
        lblTitle.setText(MessageHelper.getString("table.lbl.module.title"));
        lblActiveOrders.setText(MessageHelper.getString("table.lbl.active.orders"));
        lblDrinks.setText(MessageHelper.getString("table.lbl.drinks"));
        lblIndoor.setText(MessageHelper.getString("table.lbl.indoor"));
        lblOutdoor.setText(MessageHelper.getString("table.lbl.outdoor"));
        lblBarOne.setText(MessageHelper.getString("table.lbl.barOne"));
        lblBarTwo.setText(MessageHelper.getString("table.lbl.barTwo"));
        lblOrders.setText(MessageHelper.getString("table.lbl.orders"));

        lblTableCodeTxt.setText(MessageHelper.getString("table.lbl.table.code"));
        lblCreatedOnTxt.setText(MessageHelper.getString("table.lbl.created.on"));
        lblUserNameTxt.setText(MessageHelper.getString("table.lbl.user"));
        lblProduct.setText(MessageHelper.getString("table.lbl.product.list"));
        lblTotalPriceTxt.setText(MessageHelper.getString("table.lbl.total.price"));
        lblDiscountTxt.setText(MessageHelper.getString("table.lbl.discount"));
        lblFinalPriceTxt.setText(MessageHelper.getString("table.lbl.final.price"));

        colTable.setText(MessageHelper.getString("table.col.table"));
        colCreated.setText(MessageHelper.getString("table.col.time"));
        colUser.setText(MessageHelper.getString("table.col.user"));
        colItems.setText(MessageHelper.getString("table.col.items"));

        colProductName.setText(MessageHelper.getString("table.col.name"));
        colProductPrice.setText(MessageHelper.getString("table.col.price"));
        colProductQuantity.setText(MessageHelper.getString("table.col.quantity"));
        colTotalProductPrice.setText(MessageHelper.getString("table.col.total.price"));

        tvOrders.setPlaceholder(new Label(MessageHelper.getString("table.tb.order.holder")));
        tvProducts.setPlaceholder(new Label(MessageHelper.getString("table.tb.product.holder")));

        btnSticker.setText(MessageHelper.getString("table.btn.sticker"));
        btnComplete.setText(MessageHelper.getString("table.btn.complete"));
        btnReject.setText(MessageHelper.getString("table.btn.reject"));
    }

    private void setUpOrderTable() {
        colTable.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getTableCode() != null ? o.getValue().getTableCode() : "-"));
        colCreated.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getCreatedOn() != null ? o.getValue().getCreatedOn().format(DATE_FORMATTER) : "-"));
        colUser.setCellValueFactory(o -> new SimpleStringProperty(o.getValue().getUser() != null ? o.getValue().getUser().getFullName() : "-"));
        colItems.setCellValueFactory(o -> new SimpleObjectProperty<>(o.getValue().getProducts() != null ? o.getValue().getTotalQuantity() : 0));
        tvOrders.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tvOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showOrderData(newSelection);
                gpOrderData.setVisible(true);
            } else {
                gpOrderData.setVisible(false);
            }
        });
    }

    private void setUpProductTable() {
        colProductName.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName() != null ? p.getValue().getName() : "-"));
        colProductPrice.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getPrice() != null ? p.getValue().getPrice() : BigDecimal.ZERO));
        colProductQuantity.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getQuantity() != null ? p.getValue().getQuantity() : 1));
        colTotalProductPrice.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getSumPrice() != null ? p.getValue().getSumPrice() : BigDecimal.ZERO));

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

        colTotalProductPrice.setCellFactory(column -> new TableCell<>() {
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

        tvProducts.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }


    private void setActiveProducts(List<Order> orders) {
        Map<String, Integer> productsMap = new HashMap<>();

        for (Order order : orders) {
            if (order.getProducts() != null) {
                for (Product p : order.getProducts()) {

                    String name = p.getName();
                    int qty = p.getQuantity() != null ? p.getQuantity() : 1;

                    if (productsMap.containsKey(name)) {
                        int current = productsMap.get(name);
                        productsMap.put(name, current + qty);
                    } else {
                        productsMap.put(name, qty);
                    }
                }
            }
        }

        ObservableList<String> drinkList = FXCollections.observableArrayList();

        productsMap.forEach((product, totalQty) -> {
            drinkList.add(product + " x" + totalQty);
        });

        lvProducts.setItems(drinkList);
    }

    private void showOrderData(Order order) {
        lblTableCode.setText(order.getTableCode() != null ? order.getTableCode() : "-");
        lblCreatedOn.setText(order.getCreatedOn() != null ? order.getCreatedOn().format(DATE_FORMATTER) : "-");
        lblUserName.setText(order.getUser() != null ? order.getUser().getFullName() : "-");
        lblTotalPrice.setText((order.getTotalPrice() != null) ? order.getTotalPrice() + MessageHelper.getString("currency.symbol") : "/");

        lblFinalPrice.setText((order.getFinalPrice() != null) ? order.getFinalPrice() + MessageHelper.getString("currency.symbol") : "/");
        ObservableList<String> productStrings = FXCollections.observableArrayList();

        if (order.getHasDiscount() != null) {
            fiDiscount.getStyleClass().removeAll("icon-status-active", "icon-status-inactive");

            if (order.getHasDiscount()) {
                fiDiscount.getStyleClass().add("icon-status-active");
            } else {
                fiDiscount.getStyleClass().add("icon-status-inactive");
            }
        }

        if (order.getProducts() != null) {
            for (Product p : order.getProducts()) {
                int qty = p.getQuantity() != null ? p.getQuantity() : 1;
                productStrings.add(p.getName() + " x" + qty);
            }
        }

        if (order.getProducts() != null && !order.getProducts().isEmpty()) {
            ObservableList<Product> observableListProducts = FXCollections.observableArrayList(order.getProducts());
            tvProducts.setItems(observableListProducts);
        }

    }

    private void setOrderCount(Integer size) {
        lblOrderCounter.setText(size.toString());
    }


    private void generateTableStickerDialog() {
        List<String> tableCodes = new ArrayList<>(tableButtons.keySet());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(tableCodes.isEmpty() ? null : tableCodes.get(0), tableCodes);
        dialog.setTitle(MessageHelper.getString("table.dialog.table.choice.title"));
        dialog.setHeaderText(MessageHelper.getString("table.dialog.table.choice.header"));
        dialog.setContentText(MessageHelper.getString("table.dialog.table.choice.text"));

        dialog.showAndWait().ifPresent(selected -> {
            try {

                BarcodeData barcodeData = new BarcodeData();
                barcodeData.setTableCode(selected);
                barcodeData.setClientCode(CugomatFXApplication.getCurrentWorker().getClient().getCode());
                barcodeData.setLocationSecret(CugomatFXApplication.getCurrentWorker().getClient().getLocationSecret());

                String webUrl = CugomatFXApplication.getCurrentWorker().getClient().getWebPage();
                StickerGenerator generator = new StickerGenerator();
                generator.createTableStickerPdf(barcodeData, selected, webUrl);
                DialogHelper.showAlert(MessageHelper.getString("msg.created"), MessageHelper.getString("table.msg.created.sticker") + selected + " " + MessageHelper.getString("table.msg.created.sticker.success"), AlertLevel.INFO);
            } catch (Exception e) {
                e.printStackTrace();
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("table.msg.created.sticker.error"), AlertLevel.ERROR);
            }
        });
    }

    public void onCloseView() {
        stop();
    }
}
