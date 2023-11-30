package com.farad;

import com.farad.tables.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ShowUsersController {
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


    public void setTable(List<User> userList) {
        ObservableList<User> users = FXCollections.observableList(userList);

        idTable.setCellValueFactory(new PropertyValueFactory<User, Integer>("id"));
        usernameTable.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        passwordTable.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        roleTable.setCellValueFactory(new PropertyValueFactory<User, Integer>("role"));

        tableUsers.setItems(users);
    }
}
