package com.farad.controllers;

import com.farad.tables.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.farad.StartPage.getAlert;

public class EdCustomerController {
    @FXML
    private TextField emailField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField patronymicField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField surnameField;
    private int id;
    private String name;
    private String surname;
    private String patronymic;
    private String phone;
    private String email;
    private Stage dialogStage;
    private Customer result = null;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    @FXML
    void save() {
        if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() || patronymicField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }

        try {
            id = 0;
            name = nameField.getText().trim();
            surname = surnameField.getText().trim();
            patronymic = patronymicField.getText().trim();
            phone = phoneField.getText().trim();
            email = emailField.getText().trim();
        } catch (Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }

        result = new Customer(id, name, surname, patronymic, phone, email);
        dialogStage.close();
    }

    @FXML
    void cancel() {
        dialogStage.close();
    }

    Customer getResult () {
        return result;
    }

    void setFields(String name, String surname, String patronymic, String phone, String email) {
        setNameField(name);
        setSurnameField(surname);
        setPatronymicField(patronymic);
        setPhoneField(phone);
        setEmailField(email);
    }

    public void setEmailField(String s) {
        emailField.setText(s);
    }

    public void setNameField(String s) {
        nameField.setText(s);
    }

    public void setPatronymicField(String s) {
        patronymicField.setText(s);
    }

    public void setPhoneField(String s) {
        phoneField.setText(s);
    }

    public void setSurnameField(String s) {
        surnameField.setText(s);
    }
}
