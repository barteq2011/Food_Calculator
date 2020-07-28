package mainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import mainPackage.dialogs.Alerts;

public final class AddingDialogController {
    @FXML
    private TextField gramatureField;

    public int processResults() {
        String enteredGramature = gramatureField.getText();
        try {
            // Cast entered gramature, which default is String to Integer
            int gramature = Integer.parseInt(enteredGramature);
            // Gramature can't be negative
            if (gramature < 0) throw new Exception();
            // Return gramature if everything is ok
            return gramature;
        } catch (Exception e) {
            if (enteredGramature.length() > 0) {
                Alerts.showError("Incorrect gramature!");
            } else
                Alerts.showError("Gramature field can't be empty!");
        }
        // Return error code if something went wrong
        return -1;
    }
}
