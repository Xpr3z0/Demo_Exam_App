package com.example.controller.manager_tabs;

import com.example.bdclient.Database;
import com.example.controller.manager_tabs.util.FaultType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

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

    public void initialize() {
        calculateStatistics();
        setupFaultsTable();
    }

    private void calculateStatistics() {

        try (Connection connection = DriverManager.getConnection(
                Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {

            int numOfCompletedRequests = getNumOfCompletedRequests(connection);
            numOfCompletedRequestsTF.setText(String.valueOf(numOfCompletedRequests));

            double avgTime = getAverageTime(connection);
            avgTimeTF.setText(String.format("%.2f", avgTime));

            fillFaultsTable(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getNumOfCompletedRequests(Connection connection) throws SQLException {
        int numOfCompletedRequests = 0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM reports")) {
            if (resultSet.next()) {
                numOfCompletedRequests = resultSet.getInt(1);
            }
        }
        return numOfCompletedRequests;
    }

    private double getAverageTime(Connection connection) throws SQLException {
        double avgTime = 0.0;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT AVG(time) FROM reports")) {
            if (resultSet.next()) {
                avgTime = resultSet.getDouble(1);
            }
        }
        return avgTime;
    }

    private void fillFaultsTable(Connection connection) throws SQLException {
        ObservableList<FaultType> faultTypes = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT repair_type, COUNT(*) AS count FROM reports GROUP BY repair_type");
        while (resultSet.next()) {
            String repairType = resultSet.getString("repair_type");
            int count = resultSet.getInt("count");
            FaultType faultType = new FaultType(repairType, count);
            faultTypes.add(faultType);
        }
        resultSet.close();
        statement.close();

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


