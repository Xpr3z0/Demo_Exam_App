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

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);
            connection.setAutoCommit(false); // Отключаем автокоммит для управления транзакцией

            // Вставка данных в таблицу requests
            String insertRequestsSql = "INSERT INTO requests (equip_num, equip_type, problem_desc, status) VALUES (?, ?, ?, ?)";
            PreparedStatement insertRequestsStatement = connection.prepareStatement(insertRequestsSql);

            insertRequestsStatement.setString(1, serialNumberField.getText());
            insertRequestsStatement.setString(2, equipTypeField.getText());
            insertRequestsStatement.setString(3, descTextArea.getText());
            insertRequestsStatement.setString(4, "Новая");

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
