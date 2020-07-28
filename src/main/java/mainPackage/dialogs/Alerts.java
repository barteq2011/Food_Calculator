package mainPackage.dialogs;


import javafx.scene.control.Alert;

// Class used for reduce redundation with showing errors and other small dialog windows
public final class Alerts {
    public static void showError(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showInfo(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
