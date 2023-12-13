package com.farad.controllers;

import com.farad.tables.Car;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static com.farad.StartPage.getAlert;

public class EdCarController {
    @FXML
    private TextField amountField;

    @FXML
    private TextField brandField;

    @FXML
    private TextField colorField;

    @FXML
    private TextField equipmentField;

    @FXML
    private TextField fuelField;

    @FXML
    private TextField modelField;

    @FXML
    private TextField priceField;
    private int id;
    private String brand;
    private String model;
    private String equipment;
    private String color;
    private String fuel;
    private int price;
    private int amount;
    private Car result = null;
    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    @FXML
    void save() {
        if (brandField.getText().isEmpty() || modelField.getText().isEmpty() || equipmentField.getText().isEmpty() ||
                colorField.getText().isEmpty() || fuelField.getText().isEmpty() || priceField.getText().isEmpty() ||
                    amountField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }

        try {
            id = 0;
            brand = brandField.getText().trim();
            model = modelField.getText().trim();
            equipment = equipmentField.getText().trim();
            color = colorField.getText().trim();
            fuel = fuelField.getText().trim();
            price = Integer.parseInt(priceField.getText().trim());
            amount = Integer.parseInt(amountField.getText().trim());
        } catch (Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }

        result = new Car(id, brand, model, equipment, color, fuel, price, amount);
        dialogStage.close();
    }

    @FXML
    void cancel() {
        dialogStage.close();
    }

    Car getResult () {
        return result;
    }

    void setFields(String brand, String model, String equipment, String color, String fuel, int price, int amount) {
        setBrandField(brand);
        setModelField(model);
        setEquipmentField(equipment);
        setColorField(color);
        setFuelField(fuel);
        setPriceField(String.valueOf(price));
        setAmountField(String.valueOf(amount));
    }

    public void setAmountField(String s) {
        amountField.setText(s);
    }

    public void setBrandField(String s) {
        brandField.setText(s);
    }

    public void setColorField(String s) {
        colorField.setText(s);
    }

    public void setEquipmentField(String s) {
        equipmentField.setText(s);
    }

    public void setFuelField(String s) {
        fuelField.setText(s);
    }

    public void setModelField(String s) {
        modelField.setText(s);
    }

    public void setPriceField(String s) {
        priceField.setText(s);
    }
}
