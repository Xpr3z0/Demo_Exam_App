package com.example.controller.operator_tabs;

import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Контроллер для вкладки "Новая заявка" для оператора.
 * Управляет логикой добавления новой заявки в базу данных.
 */
public class AddRequestTabController implements Initializable {
    // Поле для ввода имени клиента
    public TextField clientNameField;

    // Поле для ввода номера телефона клиента
    public TextField clientPhoneField;

    // Поле для ввода серийного номера оборудования
    public TextField serialNumberField;

    // Поле для ввода типа оборудования
    public TextField equipTypeField;

    // Поле для ввода описания проблемы
    public TextArea descTextArea;

    // Кнопка для создания новой заявки
    public Button createRequestBtn;

    // Кнопка для очистки всех полей
    public Button clearFieldsBtn;

    // Экземпляр базы данных для взаимодействия с ней
    private Database database;

    /**
     * Метод инициализации контроллера.
     * Вызывается после загрузки FXML-файла.
     * @param location URL местоположения
     * @param resources Ресурсный пакет для локализации
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
    }

    /**
     * Очищает все поля формы для ввода новой заявки.
     */
    private void clearFields() {
        clientNameField.clear();
        clientPhoneField.clear();
        serialNumberField.clear();
        equipTypeField.clear();
        descTextArea.clear();
    }

    /**
     * Обработчик нажатия кнопки "Очистить".
     * Вызывает метод для очистки всех полей формы.
     */
    public void onActionClear() {
        clearFields();
    }

    /**
     * Обработчик нажатия кнопки "Добавить".
     * Добавляет новую заявку в таблицы базы данных requests и request_regs.
     */
    @FXML
    public void onActionAdd() {
        // Проверка на заполненность всех полей
        if (clientNameField.getText().isEmpty() || clientPhoneField.getText().isEmpty() ||
                serialNumberField.getText().isEmpty() || equipTypeField.getText().isEmpty() ||
                descTextArea.getText().isEmpty()) {
            MyAlert.showErrorAlert("Все поля должны быть заполнены!");
            return;
        }

        boolean success = false;
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            success = addRequest(connection, clientNameField.getText(), clientPhoneField.getText(), serialNumberField.getText(),
                    equipTypeField.getText(), descTextArea.getText());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (success) {
            MyAlert.showInfoAlert("Запись добавлена успешно.");
            clearFields();
        } else {
            MyAlert.showErrorAlert("Ошибка при добавлении записи.");
        }
    }

    /**
     * Добавляет новую заявку в таблицы базы данных requests и request_regs.
     * @param connection Подключение к базе данных
     * @param clientName Имя клиента
     * @param clientPhone Телефон клиента
     * @param serialNumber Серийный номер оборудования
     * @param equipType Тип оборудования
     * @param problemDesc Описание проблемы
     * @return true, если добавление прошло успешно, иначе false
     */
    public boolean addRequest(Connection connection, String clientName, String clientPhone, String serialNumber, String equipType, String problemDesc) {
        try {
            connection.setAutoCommit(false); // Отключаем автокоммит для управления транзакцией

            String insertRequestsSql = "INSERT INTO requests (equip_num, equip_type, problem_desc, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertRequestsStatement = connection.prepareStatement(insertRequestsSql)) {
                insertRequestsStatement.setString(1, serialNumber);
                insertRequestsStatement.setString(2, equipType);
                insertRequestsStatement.setString(3, problemDesc);
                insertRequestsStatement.setString(4, "Новая");
                insertRequestsStatement.executeUpdate();
            }

            int generatedId = -1;
            String generatedIdSql = "SELECT LASTVAL()";
            try (PreparedStatement generatedIdStatement = connection.prepareStatement(generatedIdSql)) {
                ResultSet resultSet = generatedIdStatement.executeQuery();
                if (resultSet.next()) {
                    generatedId = resultSet.getInt(1);
                }
            }

            String insertRequestRegsSql = "INSERT INTO request_regs (request_id, client_name, client_phone, date_start) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertRequestRegsStatement = connection.prepareStatement(insertRequestRegsSql)) {
                insertRequestRegsStatement.setInt(1, generatedId);
                insertRequestRegsStatement.setString(2, clientName);
                insertRequestRegsStatement.setString(3, clientPhone);
                insertRequestRegsStatement.setDate(4, Date.valueOf(LocalDate.now()));
                insertRequestRegsStatement.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Откатываем изменения в случае ошибки
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
