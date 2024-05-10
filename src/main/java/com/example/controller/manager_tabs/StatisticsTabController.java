package com.example.controller.manager_tabs;

import com.example.util.Database;
import com.example.util.FaultType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.xml.crypto.Data;
import java.sql.*;

public class StatisticsTabController {

    @FXML
    private TextField numOfCompletedRequestsTF;

    @FXML
    private TextField avgTimeTF;

    @FXML
    private TableView<FaultType> typesOfFaultsTable;

    @FXML
    private TableColumn<FaultType, String> faultTypeColumn;

    @FXML
    private TableColumn<FaultType, Integer> faultCountColumn;
    private Database database;

    public void initialize() {
        database = Database.getInstance();
        calculateStatistics();
        setupFaultsTable();
    }

    private void calculateStatistics() {
        numOfCompletedRequestsTF.setText(database.singleValueQuery("SELECT COUNT(*) FROM reports"));

        double avgTime = 0;
        try {
            avgTime = Double.parseDouble(database.singleValueQuery("SELECT AVG(time) FROM reports"));
        } catch (NullPointerException e) {
            System.out.println("Отчёты отсутствуют");;
        }
        avgTimeTF.setText(String.format("%.2f", avgTime));

        fillFaultsTable();
    }


    private void fillFaultsTable()  {
        ObservableList<FaultType> faultTypes = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT repair_type, COUNT(*) AS count FROM reports GROUP BY repair_type")) {
                    while (resultSet.next()) {
                        String repairType = resultSet.getString("repair_type");
                        int count = resultSet.getInt("count");
                        FaultType faultType = new FaultType(repairType, count);
                        faultTypes.add(faultType);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Заполняем таблицу данными
        typesOfFaultsTable.getItems().setAll(faultTypes);
    }


    @FXML
    private void onRefresh() {
        calculateStatistics();
    }

    private void setupFaultsTable() {
        faultTypeColumn.setCellValueFactory(new PropertyValueFactory<>("repairType"));
        faultCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

}


