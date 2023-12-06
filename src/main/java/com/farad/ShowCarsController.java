package com.farad;

import com.farad.tables.Customer;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.farad.StartPage.getAlert;

public class ShowCarsController {
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
    private Pane creatingPane;

    @FXML
    private TextField emailField;

    @FXML
    private TextField emailFilterField;

    @FXML
    private Pane filtersPane;

    @FXML
    private TextField idField;

    @FXML
    private TextField idFilterField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField nameFilterField;

    @FXML
    private TextField patronymicField;

    @FXML
    private TextField patronymicFilterField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField phoneFilterField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField surnameField;

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
    };


    public void setTable(List<Customer> customersList) {
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

    void edMode(boolean tf) {
        if (tf) {
            creatingPane.setVisible(true);
            addButton.setDisable(true);
            buttonBarChangeDel.setDisable(true);
            buttonBarSaveCancel.setDisable(true);
            filtersPane.setDisable(true);
        } else {
            creatingPane.setVisible(false);
            addButton.setDisable(false);
            buttonBarChangeDel.setDisable(false);
            buttonBarSaveCancel.setDisable(false);
            filtersPane.setDisable(false);
        }
    }

    @FXML //кнопка Добавить
    void addButton() {
        edMode(true);

        saveButton.setOnAction(this::createCustomer);
    }

    void createCustomer(ActionEvent event) {
        if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() || patronymicField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }

        if(idField.getText().isEmpty()) {
            idField.setText("0");
        }

        try {
            Customer customer = new Customer(Integer.parseInt(idField.getText()), nameField.getText().trim(), surnameField.getText().trim(), patronymicField.getText().trim(),
                    phoneField.getText().trim(), emailField.getText().trim());
            customers.add(customer);
        } catch (Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }

        idField.clear();
        nameField.clear();
        surnameField.clear();
        patronymicField.clear();
        phoneField.clear();
        emailField.clear();

        edMode(false);

        buttonBarSaveCancel.setVisible(true);
    }

    @FXML //кнопка Отмена в временной панельке
    void cancel() {
        idField.clear();
        nameField.clear();
        surnameField.clear();
        patronymicField.clear();
        phoneField.clear();
        emailField.clear();

        edMode(false);
    }

    @FXML //кнопка Изменить
    void changeButton() {
        selectedItem = tableCustomers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент");
            return;
        }

        idField.setText(String.valueOf(selectedItem.getId()));
        nameField.setText(selectedItem.getName());
        surnameField.setText(selectedItem.getSurname());
        patronymicField.setText(String.valueOf(selectedItem.getPatronymic()));
        phoneField.setText(selectedItem.getPhone());
        emailField.setText(selectedItem.getEmail());

        edMode(true);

        saveButton.setOnAction(this::changeUser);
    }

    void changeUser(ActionEvent event) {
        if (nameField.getText().isEmpty() || surnameField.getText().isEmpty() || patronymicField.getText().isEmpty() || phoneField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }

        if(idField.getText().isEmpty()) {
            idField.setText("0");
        }

        try {
            Customer customer = new Customer(Integer.parseInt(idField.getText()), nameField.getText().trim(), surnameField.getText().trim(), patronymicField.getText().trim(),
                    phoneField.getText().trim(), emailField.getText().trim());
            customers.set(customers.indexOf(selectedItem), customer);
        } catch (Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }
        tableCustomers.refresh();

        idField.clear();
        nameField.clear();
        surnameField.clear();
        patronymicField.clear();
        phoneField.clear();
        emailField.clear();

        edMode(false);

        buttonBarSaveCancel.setVisible(true);
    }

    @FXML //кнопка Удалить
    void deleteButton() {
        selectedItem = tableCustomers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить " + selectedItem.getName() + " " + selectedItem.getSurname() + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            customers.remove(selectedItem);

            buttonBarSaveCancel.setVisible(true);
        }

    }

    @FXML
    void saveChanges() throws InterruptedException {
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
    void cancelChanges() {
        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите отменить изменения?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            initialize();
            buttonBarSaveCancel.setVisible(false);
        }
    }
}
