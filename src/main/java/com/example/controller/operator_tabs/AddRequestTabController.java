package com.example.controller.operator_tabs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.dialogs.MyAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddRequestTabController implements Initializable {
    public BorderPane root;
    public TextField clientNameField;
    public TextField clientPhoneField;
    public TextField serialNumberField;
    public TextField equipTypeField;
    public TextArea descTextArea;
    public TextArea commentsTextArea;
    public Button createRequestBtn;
    public Button clearFieldsBtn;
    private ClientPostgreSQL clientPostgreSQL;
    private Connection connection = null;

    private final String DB_URL = "jdbc:postgresql://localhost:8888/postgres";
    private final String LOGIN = "postgres";
    private final String PASSWORD = "root";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void clearFields() {
        clientNameField.clear();
        clientPhoneField.clear();
        serialNumberField.clear();
        equipTypeField.clear();
        descTextArea.clear();
        commentsTextArea.clear();
    }

    public void onActionClear(ActionEvent event) {
        clearFields();
    }

    @FXML
    public void onActionAdd(ActionEvent event) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            connection.setAutoCommit(false); // Отключаем автокоммит для управления транзакцией

            // Вставка данных в таблицу requests
            String insertRequestsSql = "INSERT INTO requests (equip_num, equip_type, problem_desc, request_comments, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertRequestsStatement = connection.prepareStatement(insertRequestsSql);

            insertRequestsStatement.setString(1, serialNumberField.getText());
            insertRequestsStatement.setString(2, equipTypeField.getText());
            insertRequestsStatement.setString(3, descTextArea.getText());
            insertRequestsStatement.setString(4, commentsTextArea.getText());
            insertRequestsStatement.setString(5, "Новая");

            insertRequestsStatement.executeUpdate();

            // Получаем сгенерированный ключ (id) новой записи
            int generatedId = -1;
            try (PreparedStatement generatedIdStatement = connection.prepareStatement("SELECT LASTVAL()")) {
                ResultSet resultSet = generatedIdStatement.executeQuery();
                if (resultSet.next()) {
                    generatedId = resultSet.getInt(1);
                }
            }

            // Вставка данных в таблицу request_regs
            String insertRequestRegsSql = "INSERT INTO request_regs (request_id, client_name, client_phone, date_start) VALUES (?, ?, ?, ?)";
            PreparedStatement insertRequestRegsStatement = connection.prepareStatement(insertRequestRegsSql);
            insertRequestRegsStatement.setInt(1, generatedId); // Используем сгенерированный id
            insertRequestRegsStatement.setString(2, clientNameField.getText());
            insertRequestRegsStatement.setString(3, clientPhoneField.getText());
            insertRequestRegsStatement.setDate(4, Date.valueOf(LocalDate.now())); // Пример: устанавливаем текущую дату

            insertRequestRegsStatement.executeUpdate();

            // Завершение транзакции и фиксация изменений в базе данных
            connection.commit();
            MyAlert.showInfoAlert("Запись добавлена успешно.");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при добавлении записи.");
            if (connection != null) {
                try {
                    connection.rollback(); // Откатываем изменения в случае ошибки
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Включаем обратно автокоммит
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
