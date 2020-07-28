module mainPackage {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens mainPackage to javafx.fxml;
    exports mainPackage;
}