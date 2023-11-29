module com.farad.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.farad.client to javafx.fxml;
    exports com.farad.client;
}