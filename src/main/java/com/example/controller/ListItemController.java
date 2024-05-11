package com.example.controller;

import com.example.util.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.*;

/**
 * Контроллер для отдельного элемента списка заявок.
 * Управляет отображением информации об одной заявке в виде элемента ListView.
 */
public class ListItemController {

    @FXML
    private Label requestNumberLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label clientNameLabel;

    // Идентификатор заявки
    private int requestNumber;

    /**
     * Устанавливает идентификатор заявки и обновляет метку с номером заявки.
     * Затем вызывает обновление информации из базы данных.
     *
     * @param requestNumber Идентификатор заявки для отображения
     */
    public void setRequestNumber(int requestNumber) {
        this.requestNumber = requestNumber;
        requestNumberLabel.setText("Заявка №" + requestNumber);
        updateFromDatabase(requestNumber);
    }

    /**
     * Устанавливает описание заявки в метку.
     *
     * @param description Описание заявки
     */
    public void setDescription(String description) {
        descriptionLabel.setText(description);
    }

    /**
     * Устанавливает имя клиента в метку.
     *
     * @param clientName Имя клиента, связанного с заявкой
     */
    public void setClientName(String clientName) {
        clientNameLabel.setText("Клиент: " + clientName);
    }

    /**
     * Обновляет информацию о заявке, загружая ее из базы данных по идентификатору заявки.
     *
     * @param requestNumber Идентификатор заявки, данные которой требуется обновить
     */
    public void updateFromDatabase(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            // SQL-запрос для получения описания проблемы и имени клиента
            String query = "SELECT r.problem_desc, rr.client_name " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "WHERE r.id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, requestNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String problemDesc = resultSet.getString("problem_desc");
                        String clientName = resultSet.getString("client_name");

                        // Устанавливаем описание и имя клиента в соответствующие метки
                        setDescription(problemDesc);
                        setClientName(clientName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
