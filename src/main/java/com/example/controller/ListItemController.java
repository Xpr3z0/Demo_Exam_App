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
        clientNameLabel.setText("Клиент: " + clientName);
    }

    @FXML
    private void showDetails() {
        // Реализуйте логику для отображения подробной информации о заявке
        System.out.println("Show details clicked for request number: " + requestNumberLabel.getText());
    }

    public void updateFromDatabase(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT r.problem_desc, rr.client_name " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "WHERE r.id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, requestNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String problemType = resultSet.getString("problem_desc");
                        String clientName = resultSet.getString("client_name");

                        setDescription(problemType); // Устанавливаем problemType вместо description
                        setClientName(clientName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
