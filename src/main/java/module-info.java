module com.farad.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.farad to javafx.fxml;
    exports com.farad;
    exports com.farad.tables;
    opens com.farad.tables to javafx.fxml;
    exports com.farad.controllers;
    opens com.farad.controllers to javafx.fxml;
    exports com.farad.db;
    opens com.farad.db to javafx.fxml;
}