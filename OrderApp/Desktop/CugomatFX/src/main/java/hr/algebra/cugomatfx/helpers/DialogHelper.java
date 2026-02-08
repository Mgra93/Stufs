package hr.algebra.cugomatfx.helpers;

import hr.algebra.cugomatfx.enums.AlertLevel;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;

import java.util.List;
import java.util.Optional;

public class DialogHelper {
    public static void showAlert(String title, String message, AlertLevel level) {
        Platform.runLater(() -> {
            AlertType type;

            switch (level) {
                case AlertLevel.WARNING -> type = AlertType.WARNING;
                case AlertLevel.ERROR -> type = AlertType.ERROR;
                default -> type = AlertType.INFORMATION;
            }

            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static <T> Optional<T> showChoiceDialog(String title, String header, String content, List<T> itemList) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(itemList.get(0), itemList);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        return dialog.showAndWait();
    }
}
