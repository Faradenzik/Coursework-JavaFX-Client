package com.farad;

import com.farad.tables.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.farad.StartPage.getAlert;

public class EdUserController {
    @FXML
    private TextField idField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField roleField;

    private int id;
    private String username;
    private String password;
    private String role;

    private Stage dialogStage;
    private User result = null;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    void save() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || roleField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }
        if (idField.getText().isEmpty()) {
            idField.setText("0");
        }

        try {
            id = Integer.parseInt(idField.getText().trim());
            username = usernameField.getText().trim();
            password = passwordField.getText().trim();
            role = roleField.getText().toLowerCase().trim();
        } catch (Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }

        result = new User(id, username, password, role);
        dialogStage.close();
    }

    @FXML
    void cancel() {
        dialogStage.close();
    }

    User getResult () {
        return result;
    }

    public void setIdField(String s) {
        idField.setText(s);
    }

    public void setUsernameField(String s) {
        usernameField.setText(s);
    }

    public void setPasswordField(String s) {
        passwordField.setText(s);
    }

    public void setRoleField(String s) {
        roleField.setText(s);
    }
}
