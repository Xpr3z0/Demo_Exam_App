package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.sql.*;

public class ListItemController {
    private final String DB_URL = "jdbc:postgresql://localhost:8888/postgres";
    private final String LOGIN = "postgres";
    private final String PASSWORD = "root";

    @FXML
    private Label requestNumberLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label clientNameLabel;

    @FXML
    private Button detailsButton;

    public int getRequestNumber() {
        return requestNumber;
    }

    private int requestNumber;

    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
        requestNumberLabel.setText("Заявка №" + requestNumber);
        updateFromDatabase(requestNumber);
    }

    public void setDescription(String description) {
        descriptionLabel.setText(description);
    }

    public void setClientName(String clientName) {
        clientNameLabel.setText(clientName);
    }

    @FXML
    private void showDetails() {
        // Реализуйте логику для отображения подробной информации о заявке
        System.out.println("Show details clicked for request number: " + requestNumberLabel.getText());
    }

    public void updateFromDatabase(int requestNumber) {
        // Здесь вы должны использовать JDBC для получения данных из БД по requestNumber
        // и обновить соответствующие надписи в вашем контроллере
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT description, client_name FROM repair_requests WHERE request_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, requestNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String description = resultSet.getString("description");
                        String clientName = resultSet.getString("client_name");

                        setDescription(description);
                        setClientName(clientName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
