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

        // Формируем SQL-запрос для вставки новой заявки в таблицу requests
        String insertRequestsSql = String.format(
                "INSERT INTO requests (equip_num, equip_type, problem_desc, status) VALUES ('%s', '%s', '%s', '%s')",
                serialNumberField.getText(), equipTypeField.getText(), descTextArea.getText(), "Новая");

        // Выполняем запрос
        database.simpleQuery(insertRequestsSql);

        // Получаем сгенерированный ключ (id) новой заявки
        int generatedId = Integer.parseInt(database.executeQueryAndGetColumnValues("SELECT LASTVAL()").get(0));

        // Формируем SQL-запрос для вставки в таблицу request_regs
        String insertRequestRegsSql = String.format(
                "INSERT INTO request_regs (request_id, client_name, client_phone, date_start) VALUES (%d, '%s', '%s', '%s')",
                generatedId, clientNameField.getText(), clientPhoneField.getText(), Date.valueOf(LocalDate.now()));

        // Выполняем запрос
        database.simpleQuery(insertRequestRegsSql);

        // Отображаем уведомление о добавлении новой заявки
        MyAlert.showInfoAlert("Запись добавлена успешно.");
        clearFields(); // Очищаем поля после успешного добавления
    }
}
