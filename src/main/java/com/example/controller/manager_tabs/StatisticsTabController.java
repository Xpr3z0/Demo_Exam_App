package com.example.controller.manager_tabs;

import com.example.util.Database;
import com.example.util.FaultType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

/**
 * Контроллер для вкладки "Статистика" в приложении.
 * Отвечает за отображение статистических данных о заявках на ремонт.
 */
public class StatisticsTabController {

    @FXML
    private TextField numOfCompletedRequestsTF;

    @FXML
    private TextField avgTimeTF;

    @FXML
    private TableView<FaultType> typesOfFaultsTable;

    @FXML
    private TableColumn<FaultType, String> faultTypeColumn;

    @FXML
    private TableColumn<FaultType, Integer> faultCountColumn;

    private Database database;

    /**
     * Инициализирует контроллер, устанавливая соединение с базой данных и запуская начальные расчеты.
     */
    public void initialize() {
        database = Database.getInstance();
        calculateStatistics();
        setupFaultsTable();
    }

    /**
     * Выполняет расчет и отображение статистических данных:
     * - общее количество завершенных заявок;
     * - среднее время обработки заявок.
     */
    private void calculateStatistics() {
        // Получаем количество завершенных заявок
        String numOfCompleted = database.singleValueQuery("SELECT COUNT(*) FROM reports");
        numOfCompletedRequestsTF.setText(numOfCompleted);

        // Вычисляем среднее время обработки
        double avgTime = 0;
        String avgTimeStr = database.singleValueQuery("SELECT AVG(time) FROM reports");
        try {
            avgTime = Double.parseDouble(avgTimeStr);
        } catch (NullPointerException e) {
            System.out.println("Отчёты отсутствуют");;
        }
        String formattedAvgTime = String.format("%.2f", avgTime);
        avgTimeTF.setText(formattedAvgTime);

        // Заполняем таблицу данных о типах неисправностей
        fillFaultsTable();
    }

    /**
     * Заполняет таблицу статистикой типов неисправностей из базы данных.
     */
    private void fillFaultsTable() {
        ObservableList<FaultType> faultTypes = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(
                        "SELECT repair_type, COUNT(*) AS count FROM reports GROUP BY repair_type")) {
                    while (resultSet.next()) {
                        String repairType = resultSet.getString("repair_type");
                        int count = resultSet.getInt("count");
                        FaultType faultType = new FaultType(repairType, count);
                        faultTypes.add(faultType);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Заполняем таблицу данными
        typesOfFaultsTable.getItems().setAll(faultTypes);
    }

    /**
     * Обработчик для кнопки обновления.
     * Перезапускает расчет статистики при нажатии на кнопку "Обновить".
     */
    @FXML
    private void onRefresh() {
        calculateStatistics();
    }

    /**
     * Настраивает столбцы таблицы для отображения данных о типах неисправностей.
     */
    private void setupFaultsTable() {
        faultTypeColumn.setCellValueFactory(new PropertyValueFactory<>("repairType"));
        faultCountColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
    }
}
