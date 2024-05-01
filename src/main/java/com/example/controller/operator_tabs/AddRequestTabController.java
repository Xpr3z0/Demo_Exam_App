package com.example.controller.operator_tabs;

import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddRequestTabController implements Initializable {
    public TextField clientNameField;
    public TextField clientPhoneField;
    public TextField serialNumberField;
    public TextField equipTypeField;
    public TextArea descTextArea;
    public Button createRequestBtn;
    public Button clearFieldsBtn;
    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
    }

    private void clearFields() {
        clientNameField.clear();
        clientPhoneField.clear();
        serialNumberField.clear();
        equipTypeField.clear();
        descTextArea.clear();
    }

    public void onActionClear() {
        clearFields();
    }

    @FXML
    public void onActionAdd() {
        String insertRequestsSql = String.format(
                "INSERT INTO requests (equip_num, equip_type, problem_desc, status) VALUES ('%s', '%s', '%s', '%s')",
                serialNumberField.getText(), equipTypeField.getText(), descTextArea.getText(), "Новая");
        database.simpleQuery(insertRequestsSql);

        // Получаем сгенерированный ключ (id) новой записи
        int generatedId = Integer.parseInt(database.executeQueryAndGetColumnValues("SELECT LASTVAL()").get(0));

        // Вставка данных в таблицу request_regs
        String insertRequestRegsSql = String.format(
                "INSERT INTO request_regs (request_id, client_name, client_phone, date_start) VALUES (%d, '%s', '%s', '%s')",
                generatedId, clientNameField.getText(), clientPhoneField.getText(), Date.valueOf(LocalDate.now()));
        database.simpleQuery(insertRequestRegsSql);

        MyAlert.showInfoAlert("Запись добавлена успешно.");
    }
}