package com.farad;

import com.farad.tables.User;
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


public class ShowUsersController {
    private static Connection conn;

    static {
        try {
            conn = Connection.getInstance();
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
    private Pane filtersPane;

    @FXML
    private Button addButton;

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
        setFilters();
    };

    private void setFilters() {
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
    }

    private void setTable(List<User> userList) {
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edUser.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 240, 300);

        addUserStage.setScene(scene);
        addUserStage.setResizable(false);
        addUserStage.initModality(Modality.APPLICATION_MODAL);
        addUserStage.setTitle("Создание пользователя");

        EdUserController controller = loader.getController();
        controller.setDialogStage(addUserStage);

        addUserStage.showAndWait();

        if (controller.getResult() != null) {
            users.add(controller.getResult());
            setFilters();
            buttonBarSaveCancel.setVisible(true);
        }

        edMode(false);
    }

    @FXML //кнопка Изменить
    private void changeButton() throws IOException {
        selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент");
            return;
        }

        edMode(true);

        Stage addUserStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edUser.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 240, 300);

        addUserStage.setScene(scene);
        addUserStage.setResizable(false);
        addUserStage.initModality(Modality.APPLICATION_MODAL);
        addUserStage.setTitle("Редактор пользователя");

        EdUserController controller = loader.getController();
        controller.setDialogStage(addUserStage);

        controller.setIdField(String.valueOf(selectedItem.getId()));
        controller.setUsernameField(selectedItem.getUsername());
        controller.setPasswordField(selectedItem.getPassword());
        controller.setRoleField(selectedItem.getRole());

        addUserStage.showAndWait();

        if (controller.getResult() != null) {
            users.set(users.indexOf(selectedItem), controller.getResult());
            tableUsers.refresh();
            buttonBarSaveCancel.setVisible(true);
        }

        edMode(false);
    }

    @FXML //кнопка Удалить
    private void deleteButton() {
        selectedItem = tableUsers.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить " + selectedItem.getUsername() + "?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            users.remove(selectedItem);
            setFilters();
            buttonBarSaveCancel.setVisible(true);
        }
    }

    @FXML
    private void saveChanges() throws InterruptedException {
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
    private void cancelChanges() {
        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите отменить изменения?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            initialize();
            setFilters();
            buttonBarSaveCancel.setVisible(false);
        }
    }
}
