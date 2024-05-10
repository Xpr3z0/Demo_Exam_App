package com.example.controller;


import com.example.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.*;

public class ListItemController {
    @FXML
    private Label requestNumberLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label clientNameLabel;
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

    public void updateFromDatabase(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
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
