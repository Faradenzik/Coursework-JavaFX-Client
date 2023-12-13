package com.farad.controllers;

import com.farad.db.Connection;
import com.farad.tables.Customer;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.farad.StartPage.getAlert;

public class ShowCustomersController {
    private static Connection conn;

    static {
        try {
            conn = Connection.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private Button addButton;

    @FXML
    private ButtonBar buttonBarChangeDel;

    @FXML
    private ButtonBar buttonBarSaveCancel;

    @FXML
    private TextField emailFilterField;

    @FXML
    private Pane filtersPane;

    @FXML
    private TextField idFilterField;

    @FXML
    private TextField nameFilterField;

    @FXML
    private TextField patronymicFilterField;

    @FXML
    private TextField phoneFilterField;

    @FXML
    private TextField surnameFilterField;

    @FXML
    private TableView<Customer> tableCustomers;

    @FXML
    private TableColumn<?, ?> idTable;

    @FXML
    private TableColumn<?, ?> nameTable;

    @FXML
    private TableColumn<?, ?> surnameTable;

    @FXML
    private TableColumn<?, ?> patronymicTable;

    @FXML
    private TableColumn<?, ?> phoneTable;

    @FXML
    private TableColumn<?, ?> emailTable;

    private Customer selectedItem;
    private ObservableList<Customer> customers;
    private ObservableList<Customer> filteredList;
    private final ChangeListener<String> textChangeListener = (observable, oldValue, newValue) -> {
        setFilters();
    };

    private void setFilters() {
        String id = idFilterField.getText().trim();
        String name = nameFilterField.getText().trim();
        String surname = surnameFilterField.getText().trim();
        String patronymic = patronymicFilterField.getText().trim();
        String phone = phoneFilterField.getText().trim();
        String email = emailFilterField.getText().trim();

        FilteredList<Customer> filtered = new FilteredList<>(customers, customer -> true);

        filtered.setPredicate(customer -> customer.getName().toLowerCase().contains(name) && String.valueOf(customer.getId()).contains(id)
                && customer.getSurname().toLowerCase().contains(surname) && customer.getPatronymic().toLowerCase().contains(patronymic)
                && customer.getPhone().contains(phone) && customer.getEmail().contains(email));

        filteredList = FXCollections.observableList(filtered);

        if (id.isEmpty() && name.isEmpty() && surname.isEmpty() && patronymic.isEmpty() && phone.isEmpty() && email.isEmpty()) {
            tableCustomers.setItems(customers);
        } else {
            tableCustomers.setItems(filteredList);
        }
    }


    private void setTable(List<Customer> customersList) {
        customers = FXCollections.observableList(customersList);

        idTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameTable.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameTable.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patronymicTable.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        phoneTable.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailTable.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableCustomers.setItems(customers);
    }

    @FXML
    private void initialize() {
        conn.sendRequest("get customers");
        List<?> received;
        try {
            received = conn.getObjects();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Customer> customersList = (List<Customer>) received;

        setTable(customersList);

        idFilterField.textProperty().addListener(textChangeListener);
        nameFilterField.textProperty().addListener(textChangeListener);
        surnameFilterField.textProperty().addListener(textChangeListener);
        patronymicFilterField.textProperty().addListener(textChangeListener);
        phoneFilterField.textProperty().addListener(textChangeListener);
        emailFilterField.textProperty().addListener(textChangeListener);
    }

    private void edMode(boolean tf) {
        if (tf) {
            addButton.setDisable(true);
            buttonBarChangeDel.setDisable(true);
            buttonBarSaveCancel.setDisable(true);
            filtersPane.setDisable(true);
        } else {
            addButton.setDisable(false);
            buttonBarChangeDel.setDisable(false);
            buttonBarSaveCancel.setDisable(false);
            filtersPane.setDisable(false);
        }
    }

    @FXML //кнопка Добавить
    private void addButton() throws IOException {
        edMode(true);

        Stage addUserStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edCustomer.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 240, 380);

        addUserStage.setScene(scene);
        addUserStage.setResizable(false);
        addUserStage.initModality(Modality.APPLICATION_MODAL);
        addUserStage.setTitle("Создание заказчика");

        EdCustomerController controller = loader.getController();
        controller.setDialogStage(addUserStage);

        addUserStage.showAndWait();

        if (controller.getResult() != null) {
            customers.add(controller.getResult());
            setFilters();
            buttonBarSaveCancel.setVisible(true);
        }

        edMode(false);
    }

    @FXML //кнопка Изменить
    private void changeButton() throws IOException {
        selectedItem = tableCustomers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент");
            return;
        }

        edMode(true);

        Stage addCustomerStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edCustomer.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 240, 380);

        addCustomerStage.setScene(scene);
        addCustomerStage.setResizable(false);
        addCustomerStage.initModality(Modality.APPLICATION_MODAL);
        addCustomerStage.setTitle("Редактор заказчика");

        EdCustomerController controller = loader.getController();
        controller.setDialogStage(addCustomerStage);

        controller.setFields(selectedItem.getName(), selectedItem.getSurname(), selectedItem.getPatronymic(), selectedItem.getPhone(), selectedItem.getEmail());

        addCustomerStage.showAndWait();

        if (controller.getResult() != null) {
            customers.set(customers.indexOf(selectedItem), controller.getResult());
            tableCustomers.refresh();
            buttonBarSaveCancel.setVisible(true);
        }
        edMode(false);
    }

    @FXML //кнопка Удалить
    private void deleteButton() {
        selectedItem = tableCustomers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить " + selectedItem.getName() + " " + selectedItem.getSurname() + "?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            customers.remove(selectedItem);
            setFilters();
            buttonBarSaveCancel.setVisible(true);
        }
    }

    @FXML
    private void saveChanges() throws InterruptedException {
        List<?> customersList = new ArrayList<>(customers);

        conn.sendRequest("update customers");
        Thread.sleep(100);
        conn.sendObjects(customersList);

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
            setFilters();
            buttonBarSaveCancel.setVisible(false);
        }
    }
}
