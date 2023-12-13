package com.farad.controllers;

import com.farad.db.Connection;
import com.farad.tables.Car;
import com.farad.tables.Customer;
import com.farad.tables.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.farad.StartPage.getAlert;

public class ShowOrdersController {
    private static Connection conn;

    static {
        try {
            conn = Connection.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private ButtonBar buttonBarSaveCancel;

    @FXML
    private TableColumn<?, ?> dateTable;

    @FXML
    private TableColumn<?, ?> idCarTable;

    @FXML
    private TableColumn<?, ?> idCustomerTable;

    @FXML
    private TableColumn<?, ?> idTable;

    @FXML
    private TableColumn<?, ?> usernameTable;

    @FXML
    private TableColumn<?, ?> priceTable;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Order> tableOrders;

    private ObservableList<Order> orders;

    private List<Car> cars;
    private List<Customer> customers;
    private ObservableList<Order> filteredList;
    private Order selectedItem;

    private void setFilters() {
        List<String> searchValues = Arrays.asList(searchField.getText().toLowerCase().trim().split("\\s+"));

        Predicate<Order> filterPredicate = order -> searchValues.stream()
                .allMatch(value -> String.valueOf(order.getId()).contains(value)
                        || String.valueOf(order.getDate()).contains(value)
                        || String.valueOf(order.getPrice()).contains(value)
                        || order.getUsername().contains(value)
                        || order.toString().contains(value));

        ObservableList<Order> filteredOrders = orders.stream()
                .filter(filterPredicate)
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));

        if (searchField.getText().trim().isEmpty()) {
            tableOrders.setItems(orders);
        } else {
            tableOrders.setItems(filteredOrders);
        }
    }

    @FXML
    private void initialize() {
        conn.sendRequest("get orders");
        List<?> received;
        try {
            received = conn.getObjects();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Order> ordersList = (List<Order>) received;

        conn.sendRequest("get cars");
        try {
            received = conn.getObjects();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        cars = new ArrayList<>((List<Car>) received);

        conn.sendRequest("get customers");
        try {
            received = conn.getObjects();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        customers = new ArrayList<>((List<Customer>) received);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> setFilters());


        setTable(ordersList);
    }

    private void setTable(List<Order> ordersList) {
        orders = FXCollections.observableList(ordersList);

        idTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCustomerTable.setCellValueFactory(new PropertyValueFactory<>("id_customer"));
        idCarTable.setCellValueFactory(new PropertyValueFactory<>("id_cars"));
        usernameTable.setCellValueFactory(new PropertyValueFactory<>("username"));
        priceTable.setCellValueFactory(new PropertyValueFactory<>("price"));
        dateTable.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableOrders.setItems(orders);
    }

    @FXML
    private void createOrder() throws IOException {
        Stage addOrderStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edOrder.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 410, 340);

        addOrderStage.setScene(scene);
        addOrderStage.setResizable(false);
        addOrderStage.initModality(Modality.APPLICATION_MODAL);
        addOrderStage.setTitle("Создание заказа");

        EdOrderController controller = loader.getController();
        controller.setDialogStage(addOrderStage);
        controller.initialize(cars, customers);

        addOrderStage.showAndWait();

        if (controller.getResult() != null) {
            orders.add(controller.getResult());
            buttonBarSaveCancel.setVisible(true);
        }
    }

    @FXML
    private void changeButton() throws IOException {
        selectedItem = tableOrders.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент");
            return;
        }
        Stage addOrderStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edOrder.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 410, 340);

        addOrderStage.setScene(scene);
        addOrderStage.setResizable(false);
        addOrderStage.initModality(Modality.APPLICATION_MODAL);
        addOrderStage.setTitle("Редактирование заказа");

        EdOrderController controller = loader.getController();
        controller.setDialogStage(addOrderStage);
        controller.initialize(cars, customers);
        controller.setFields(selectedItem.getId_customer(), selectedItem.getId_cars(), selectedItem.getPrice());

        addOrderStage.showAndWait();

        if (controller.getResult() != null) {
            orders.set(orders.indexOf(selectedItem), controller.getResult());
            tableOrders.refresh();
            buttonBarSaveCancel.setVisible(true);
        }
    }

    @FXML
    private void deleteButton() {
        selectedItem = tableOrders.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить заказ " + selectedItem.getId()+ "?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            orders.remove(selectedItem);
            buttonBarSaveCancel.setVisible(true);
        }
    }

    @FXML
    private void saveChanges() throws InterruptedException {
        List<Order> ordersList = new ArrayList<>(orders);

        conn.sendRequest("update orders");
        Thread.sleep(100);
        conn.sendObjects(ordersList);

        String result = conn.getRequest();
        if (result.equals("success")) {
            initialize();
        } else {
            getAlert("error", result);
            return;
        }

        buttonBarSaveCancel.setVisible(false);
    }

    @FXML
    private void cancelChanges() {
        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите отменить изменения?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            initialize();
            buttonBarSaveCancel.setVisible(false);
        }
    }
}
