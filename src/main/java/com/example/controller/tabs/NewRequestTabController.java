package com.example.controller.tabs;

import com.example.bdclient.ClientPostgreSQL;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class NewRequestTabController implements Initializable {
    public BorderPane root;

    public TextField clientNameField;
    public TextField clientPhoneField;
    public TextField serialNumberField;
    public TextField equipTypeField;
    public TextField descriptionTextField;
    public TextField notesTextField;
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
        descriptionTextField.clear();
        notesTextField.clear();
    }

    public void onActionClear(ActionEvent event) {
        clearFields();
    }
    
    @FXML
    public void onActionAdd(ActionEvent event) {
        try {
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO repair_requests (client_name, client_phone, equipment_serial_number, equipment_type, description, notes) VALUES (?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, clientNameField.getText());
            preparedStatement.setString(2, clientPhoneField.getText());
            preparedStatement.setString(3, serialNumberField.getText());
            preparedStatement.setString(4, equipTypeField.getText());
            preparedStatement.setString(5, descriptionTextField.getText());
            preparedStatement.setString(6, notesTextField.getText());
            String resultStr = preparedStatement.toString();
            preparedStatement.execute();
//            ClientPostgreSQL.getInstance().simpleQuery("public", resultStr);
            preparedStatement.close();

            showInfoAlert("Запись добавлена успешно.");
            clearFields();

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            showErrorAlert("Ошибка при добавлении записи.");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
