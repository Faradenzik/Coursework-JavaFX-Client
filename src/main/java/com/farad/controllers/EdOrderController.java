package com.farad.controllers;

import com.farad.tables.Car;
import com.farad.tables.Customer;
import com.farad.tables.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.*;

import static com.farad.StartPage.getAlert;

public class EdOrderController {
    private Stage dialogStage;
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private ListView<String> carsListView;

    @FXML
    private Spinner<Integer> countField;

    @FXML
    private ComboBox<Integer> idCarField;

    @FXML
    private ComboBox<Integer> idCustomerField;

    @FXML
    private TextField priceField;

    Map<Integer, Integer> id_count_cars = new HashMap<>();

    private List<Car> carsLst;

    private Order result = null;

    private void comboCarListener() {
        int max = 0;
        for (Car car : carsLst) {
            if (car.getId() == idCarField.getValue()) {
                max = car.getAmount();
                break;
            }
        }

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, max, 0);
        countField.setValueFactory(valueFactory);
    }

    @FXML
    void initialize(List<Car> cars, List<Customer> customers) {
        carsLst = cars;
        List<Integer> idCustomers = new ArrayList<>();
        for (Customer customer : customers) {
            idCustomers.add(customer.getId());
        }

        List<Integer> idCars = new ArrayList<>();
        for (Car car : carsLst) {
            idCars.add(car.getId());
        }

        ObservableList<Integer> idCustomersList = FXCollections.observableList(idCustomers);
        idCustomerField.setItems(idCustomersList);
        ObservableList<Integer> idCarsList = FXCollections.observableList(idCars);
        idCarField.setItems(idCarsList);
        idCarField.setOnAction(event -> comboCarListener());
    }

    @FXML
    void addCar() {
        try {
            int id = idCustomerField.getValue();
            int idCar = idCarField.getValue();
            int count = countField.getValue();
            if (count == 0) {
                getAlert("warning", "Добавьте хотя бы один экземпляр авто!");
                return;
            }
            carsListView.getItems().add(idCar + "x" + count);
            int totalPrice;
            try {
                totalPrice = Integer.parseInt(priceField.getText().trim());
            } catch (Exception e) {
                totalPrice = 0;
            }
            for (Car car : carsLst) {
                if (car.getId() == idCar) {
                    totalPrice += car.getPrice() * count;
                    break;
                }
            }

            priceField.setText(String.valueOf(totalPrice));
        } catch (NullPointerException e) {
            getAlert("warning", "Заполните все поля!");
        }
    }

    @FXML
    void deleteCar() {
        String selectedItem = carsListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить " + selectedItem + "?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            int totalPrice = Integer.parseInt(priceField.getText().trim());
            for (Car car : carsLst) {
                if (car.getId() == Integer.parseInt(selectedItem.split("x")[0])) {
                    totalPrice -= car.getPrice() * Integer.parseInt(selectedItem.split("x")[1]);
                    break;
                }
            }
            priceField.setText(String.valueOf(totalPrice));
            carsListView.getItems().remove(carsListView.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    void saveButton() {
        int id = 0;
        int idCustomer = idCustomerField.getValue();
        String username = StartPageController.username;
        for (String s : carsListView.getItems()) {
            id_count_cars.put(Integer.parseInt(s.split("x")[0]), Integer.parseInt(s.split("x")[1]));
        }
        int price = Integer.parseInt(priceField.getText().trim());
        LocalDateTime date = LocalDateTime.now();

        result = new Order(id, idCustomer, username, id_count_cars, price, date);
        dialogStage.close();
    }

    @FXML
    void cancelButton() {
        dialogStage.close();
    }

    Order getResult() {
        return result;
    }

    void setFields(int idCustomer, Map<Integer, Integer> map, int price) {
        setIdCustomerField(idCustomer);
        setCarsListView(map);
        setPriceField(price);
    }

    public void setCarsListView(Map<Integer, Integer> map) {
        List<Integer> cars = map.keySet().stream().toList();
        List<Integer> counts = map.values().stream().toList();
        for (int i = 0; i < cars.size(); i++) {
            this.carsListView.getItems().add(cars.get(i) + "x" + counts.get(i));
        }
    }

    public void setIdCustomerField(int id) {
        this.idCustomerField.setValue(id);
    }

    public void setPriceField(int price) {
        this.priceField.setText(String.valueOf(price));
    }
}
