package com.farad.controllers;

import com.farad.db.Connection;
import com.farad.tables.Car;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.*;

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
    private TextField idFilter;

    @FXML
    private TextField priceMinFilter;

    @FXML
    private TextField priceMaxFilter;

    @FXML
    private MenuButton brandFilter;

    @FXML
    private MenuButton modelFilter;

    @FXML
    private MenuButton equipmentFilter;

    @FXML
    private MenuButton colorFilter;

    @FXML
    private MenuButton fuelFilter;

    @FXML
    private CheckBox inStockFilter;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<?, ?> amountTable;

    @FXML
    private TableColumn<?, ?> brandTable;

    @FXML
    private ButtonBar buttonBarChangeDel;

    @FXML
    private ButtonBar buttonBarSaveCancel;

    @FXML
    private TableColumn<?, ?> colorTable;

    @FXML
    private TableColumn<?, ?> equipmentTable;

    @FXML
    private Pane filtersPane;

    @FXML
    private TableColumn<?, ?> fuelTable;

    @FXML
    private TableColumn<?, ?> idTable;

    @FXML
    private TableColumn<?, ?> modelTable;

    @FXML
    private TableColumn<?, ?> priceTable;

    @FXML
    private TableView<Car> tableCars;

    private Car selectedItem;
    private ObservableList<Car> cars;
    private ObservableList<Car> filteredList;
    private Set<String> comboBoxBrand = new HashSet<>();
    private Set<String> comboBoxModel = new HashSet<>();
    private Set<String> comboBoxEquipment = new HashSet<>();
    private Set<String> comboBoxColor = new HashSet<>();
    private Set<String> comboBoxFuel = new HashSet<>();

    private void setFilters() {
        String id = idFilter.getText().trim();

        List<String> brands = getListsFilters(brandFilter);
        List<String> models = getListsFilters(modelFilter);
        List<String> equipments = getListsFilters(equipmentFilter);
        List<String> colors = getListsFilters(colorFilter);
        List<String> fuels = getListsFilters(fuelFilter);

        int minPrice = priceMinFilter.getText().isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(priceMinFilter.getText());
        int maxPrice = priceMaxFilter.getText().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(priceMaxFilter.getText());

        boolean inStock = inStockFilter.isSelected();

        FilteredList<Car> filtered = new FilteredList<>(cars, car -> true);

        filtered.setPredicate(car -> (((String.valueOf(car.getId())).contains(id)) &&
                (brands.isEmpty() || brands.contains(car.getBrand())) &&
                (models.isEmpty() || models.contains(car.getModel())) &&
                (equipments.isEmpty() || equipments.contains(car.getEquipment())) &&
                (colors.isEmpty() || colors.contains(car.getColor())) &&
                (fuels.isEmpty() || fuels.contains(car.getFuel())) &&
                (minPrice <= car.getPrice()) &&
                (maxPrice >= car.getPrice()) &&
                (!inStock || car.getAmount() > 0)));

        filteredList = FXCollections.observableList(filtered);

        if (id.isEmpty() && brands.isEmpty() && models.isEmpty() && equipments.isEmpty() && colors.isEmpty() && fuels.isEmpty() &&
                priceMinFilter.getText().isEmpty() && priceMaxFilter.getText().isEmpty() && !inStock) {
            tableCars.setItems(cars);
        } else {
            tableCars.setItems(filteredList);
        }
    };


    private void setTable(List<Car> carsList) {
        cars = FXCollections.observableList(carsList);

        idTable.setCellValueFactory(new PropertyValueFactory<>("id"));
        brandTable.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelTable.setCellValueFactory(new PropertyValueFactory<>("model"));
        equipmentTable.setCellValueFactory(new PropertyValueFactory<>("equipment"));
        colorTable.setCellValueFactory(new PropertyValueFactory<>("color"));
        fuelTable.setCellValueFactory(new PropertyValueFactory<>("fuel"));
        priceTable.setCellValueFactory(new PropertyValueFactory<>("price"));
        amountTable.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableCars.setItems(cars);
    }

    private void filtersAdding(Set<String> in, MenuButton out) {
        out.getItems().clear();
        List<String> temp = new ArrayList<>(in);
        temp.sort(Comparator.naturalOrder());
        for (String s : temp) {
            CheckMenuItem item = new CheckMenuItem(s);
            item.selectedProperty().addListener((observableValue, aBoolean, t1) -> setFilters());
            out.getItems().add(item);
        }
    }

    private List<String> getListsFilters(MenuButton in) {
        List<String> out = new ArrayList<>();
        for (MenuItem item : in.getItems()) {
            if (item instanceof CheckMenuItem checkMenuItem) {
                if (checkMenuItem.isSelected()) {
                    out.add(checkMenuItem.getText());
                }
            }
        }
        return out;
    }

    @FXML
    private void initialize() {
        conn.sendRequest("get cars");
        List<?> received;
        try {
            received = conn.getObjects();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Car> carsList = (List<Car>) received;

        setTable(carsList);

        idFilter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setFilters();
            }
        });

        for (Car car : cars) {
            comboBoxBrand.add(car.getBrand());
            comboBoxModel.add(car.getModel());
            comboBoxEquipment.add(car.getEquipment());
            comboBoxColor.add(car.getColor());
            comboBoxFuel.add(car.getFuel());
        }

        filtersAdding(comboBoxBrand, brandFilter);
        filtersAdding(comboBoxModel, modelFilter);
        filtersAdding(comboBoxEquipment, equipmentFilter);
        filtersAdding(comboBoxColor, colorFilter);
        filtersAdding(comboBoxFuel, fuelFilter);

        priceMinFilter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setFilters();
            }
        });

        priceMaxFilter.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setFilters();
            }
        });

        inStockFilter.selectedProperty().addListener((observableValue, aBoolean, t1) -> setFilters());
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

        Stage addCarStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edCar.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 240, 470);

        addCarStage.setScene(scene);
        addCarStage.setResizable(false);
        addCarStage.initModality(Modality.APPLICATION_MODAL);
        addCarStage.setTitle("Создание авто");

        EdCarController controller = loader.getController();
        controller.setDialogStage(addCarStage);

        addCarStage.showAndWait();

        if (controller.getResult() != null) {
            cars.add(controller.getResult());
            setFilters();
            buttonBarSaveCancel.setVisible(true);
        }

        edMode(false);
    }

    @FXML //кнопка Изменить
    private void changeButton() throws IOException {
        selectedItem = tableCars.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент");
            return;
        }

        edMode(true);

        Stage addCarStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edCar.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root, 240, 470);

        addCarStage.setScene(scene);
        addCarStage.setResizable(false);
        addCarStage.initModality(Modality.APPLICATION_MODAL);
        addCarStage.setTitle("Редактор авто");

        EdCarController controller = loader.getController();
        controller.setDialogStage(addCarStage);

        controller.setFields(selectedItem.getBrand(), selectedItem.getModel(),
                selectedItem.getEquipment(), selectedItem.getColor(), selectedItem.getFuel(),
                selectedItem.getPrice(), selectedItem.getAmount());

        addCarStage.showAndWait();

        if (controller.getResult() != null) {
            cars.set(cars.indexOf(selectedItem), controller.getResult());
            tableCars.refresh();
            buttonBarSaveCancel.setVisible(true);
        }

        edMode(false);
    }

    @FXML //кнопка Удалить
    private void deleteButton() {
        selectedItem = tableCars.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            getAlert("warning", "Сначала выберите элемент!");
            return;
        }

        Optional<ButtonType> result = getAlert("confirmation", "Вы действительно хотите удалить " + selectedItem.getBrand() + " " + selectedItem.getModel() + "?");
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            cars.remove(selectedItem);
            setFilters();
            buttonBarSaveCancel.setVisible(true);
        }
    }

    @FXML
    private void saveChanges() throws InterruptedException {
        List<Car> carsList = new ArrayList<>(cars);

        conn.sendRequest("update cars");
        Thread.sleep(100);
        conn.sendObjects(carsList);

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
