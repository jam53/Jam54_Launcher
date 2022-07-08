module com.jam54.jam54_launcher {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jam54.jam54_launcher to javafx.fxml;
    exports com.jam54.jam54_launcher;
}