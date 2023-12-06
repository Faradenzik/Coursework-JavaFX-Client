package com.farad;

import com.farad.tables.Customer;
import com.farad.tables.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.farad.StartPage.getAlert;


public class ShowUsersController {
    private static Connection conn;

    static {
        try {
            conn = Connection.getInstance("localhost", 8080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private TableColumn<User, Integer> idTable;

    @FXML
    private TableColumn<User, String> usernameTable;

    @FXML
    private TableColumn<User, String> passwordTable;

    @FXML
    private TableColumn<User, Integer> roleTable;

    @FXML
    private TableView<User> tableUsers;

    @FXML
    private ButtonBar buttonBarChangeDel;

    @FXML
    public ButtonBar buttonBarSaveCancel;

    @FXML
    private Pane creatingPane;

    @FXML
    private Pane filtersPane;

    @FXML
    private Button addButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextField idField;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField roleField;

    @FXML
    private TextField idFilterField;

    @FXML
    private TextField usernameFilterField;

    @FXML
    private TextField passwordFilterField;

    @FXML
    private TextField roleFilterField;

    private User selectedItem;
    private ObservableList<User> users;
    private ObservableList<User> filteredList;
    private final ChangeListener<String> textChangeListener = (observable, oldValue, newValue) -> {
        String id = idFilterField.getText().trim();
        String username = usernameFilterField.getText().trim();
        String password = passwordFilterField.getText().trim();
        String role = roleFilterField.getText().trim();

        FilteredList<User> filtered = new FilteredList<>(users, user -> true);

        filtered.setPredicate(user -> user.getUsername().toLowerCase().contains(username) && String.valueOf(user.getId()).contains(id)
                && user.getPassword().toLowerCase().contains(password) && String.valueOf(user.getRole()).contains(role));

        filteredList = FXCollections.observableList(filtered);

        if (id.isEmpty() && username.isEmpty() && password.isEmpty() && role.isEmpty()) {
            tableUsers.setItems(users);
        } else {
            tableUsers.setItems(filteredList);
        }
    };


    public void setTable(List<User> userList) {
        users = FXCollections.observableList(userList);

        idTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameTable.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordTable.setCellValueFactory(new PropertyValueFactory<>("password"));
        roleTable.setCellValueFactory(new PropertyValueFactory<>("role"));

        tableUsers.setItems(users);
    }

    @FXML
    private void initialize() {
        conn.sendRequest("get users");
        List<?> received;
        try {
            received = conn.getObjects();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<User> userList = (List<User>) received;

        setTable(userList);

        idFilterField.textProperty().addListener(textChangeListener);
        usernameFilterField.textProperty().addListener(textChangeListener);
        passwordFilterField.textProperty().addListener(textChangeListener);
        roleFilterField.textProperty().addListener(textChangeListener);
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

        saveButton.setOnAction(this::createUser);
    }

    void createUser(ActionEvent event) {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || roleField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }

        if (idField.getText().isEmpty()) {
            idField.setText("0");
        }

        try {
            User user = new User(Integer.parseInt(idField.getText().trim()), usernameField.getText().trim(),
                    passwordField.getText().trim(), Integer.parseInt(roleField.getText().trim()));
            users.add(user);
        } catch(Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }

        idField.clear();
        usernameField.clear();
        passwordField.clear();
        roleField.clear();

        edMode(false);

        buttonBarSaveCancel.setVisible(true);
    }

    @FXML //кнопка Отмена в временной панельке
    void cancel() {
        idField.clear();
        usernameField.clear();
        passwordField.clear();
        roleField.clear();
        
        edMode(false);
    }

    @FXML //кнопка Изменить
    void changeButton() {
        selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент");
            return;
        }

        idField.setText(String.valueOf(selectedItem.getId()));
        usernameField.setText(selectedItem.getUsername());
        passwordField.setText(selectedItem.getPassword());
        roleField.setText(String.valueOf(selectedItem.getRole()));

        edMode(true);

        saveButton.setOnAction(this::changeUser);
    }

    void changeUser(ActionEvent event) {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty() || roleField.getText().isEmpty()) {
            getAlert("warning", "Заполните поля!");
            return;
        }

        if (idField.getText().isEmpty()) {
            idField.setText("0");
        }

        try {
            User user = new User(Integer.parseInt(idField.getText().trim()), usernameField.getText().trim(),
                    passwordField.getText().trim(), Integer.parseInt(roleField.getText().trim()));
            users.set(users.indexOf(selectedItem), user);
        } catch(Exception e) {
            getAlert("warning", "Проверьте введенные значения!");
            return;
        }

        tableUsers.refresh();

        idField.clear();
        usernameField.clear();
        passwordField.clear();
        roleField.clear();

        edMode(false);

        buttonBarSaveCancel.setVisible(true);
    }

    @FXML //кнопка Удалить
    void deleteButton() {
        selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить " + selectedItem.getUsername() + "?");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            users.remove(selectedItem);

            buttonBarSaveCancel.setVisible(true);
        }

    }

    @FXML
    void saveChanges() throws InterruptedException {
        List<?> usersList = new ArrayList<>(users);

        conn.sendRequest("update users");
        Thread.sleep(100);
        conn.sendObjects(usersList);

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
