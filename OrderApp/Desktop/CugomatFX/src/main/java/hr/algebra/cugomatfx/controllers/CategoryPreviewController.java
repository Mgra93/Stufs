package hr.algebra.cugomatfx.controllers;

import hr.algebra.cugomatfx.enums.AlertLevel;
import hr.algebra.cugomatfx.helpers.DialogHelper;
import hr.algebra.cugomatfx.helpers.MessageHelper;
import hr.algebra.cugomatfx.helpers.Navigator;
import hr.algebra.cugomatfx.models.Category;
import hr.algebra.cugomatfx.service.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryPreviewController implements Initializable {
    @FXML
    private Button btnCreateView;
    @FXML
    private Button btnUpdateView;
    @FXML
    private Button btnDelete;
    @FXML
    private Label lblTitle;
    @FXML
    private ListView<Category> lvCategory;
    @FXML
    private StackPane spContent;

    private Category selectedCategory;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setItemsText();
        getCategoryList();
        initButtons();
        initListView();
    }

    private void initButtons() {
        btnCreateView.setOnAction(e -> {
            CategoryFormController controller = Navigator.loadPaneWithController(spContent, Navigator.CATEGORY_FORM_VIEW_PATH);

            if (controller != null) {
                controller.setCategory(null);
                controller.setOnCloseCallback(() -> {
                    getCategoryList();
                    spContent.getChildren().clear();
                });
            }
        });

        btnUpdateView.setOnAction(e -> {
            if (selectedCategory != null) {
                CategoryFormController controller = Navigator.loadPaneWithController(spContent, Navigator.CATEGORY_FORM_VIEW_PATH);

                if (controller != null) {
                    controller.setCategory(selectedCategory);
                    controller.setOnCloseCallback(() -> {
                        getCategoryList();
                        spContent.getChildren().clear();
                    });
                }
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("category.preview.select"), AlertLevel.INFO);
            }
        });

        btnDelete.setOnAction(e -> {
            if (selectedCategory != null) {
                deleteCategory(selectedCategory);
            } else {
                DialogHelper.showAlert(MessageHelper.getString("msg.error"), MessageHelper.getString("category.preview.select"), AlertLevel.INFO);
            }
        });
    }

    private void initListView(){
        lvCategory.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selection) -> {
            selectedCategory = selection;
        });
    }

    private void setItemsText() {
        lblTitle.setText(MessageHelper.getString("category.preview.lbl.title"));
        btnCreateView.setText(MessageHelper.getString("category.preview.btn.create.view"));
        btnUpdateView.setText(MessageHelper.getString("category.preview.btn.update.view"));
        btnDelete.setText(MessageHelper.getString("category.preview.btn.delete"));
    }

    private void getCategoryList(){
        new Thread(() -> {
            try {
                List<Category> categoryList = ApiService.getInstance().getCategoryList();

                if (categoryList == null) {
                    categoryList = List.of();
                }

                List<Category> finalCategoryList = categoryList;
                Platform.runLater(() -> {
                    lvCategory.getItems().clear();
                    for (Category c : finalCategoryList) {
                        lvCategory.getItems().add(c);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void deleteCategory(Category category){
        new Thread(() -> {
            try {

                boolean deleted = ApiService.getInstance().deleteCategory(category.getId());

                if(deleted) {
                    DialogHelper.showAlert(MessageHelper.getString("category.alert.title.delete"),
                            MessageHelper.getString("category.alert.deleted"), AlertLevel.INFO);
                    getCategoryList();
                }else{
                    DialogHelper.showAlert(MessageHelper.getString("category.alert.error.delete"),
                            MessageHelper.getString("category.alert.deleted"), AlertLevel.WARNING);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
