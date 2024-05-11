package com.example.controller;

import com.example.util.Database;
import com.example.util.MyAlert;
import com.example.util.UniversalAddDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Универсальный контроллер для отображения таблиц в пользовательском интерфейсе.
 * Управляет отображением, добавлением и удалением записей из таблицы, взаимодействуя с базой данных.
 * <p>
 * Класс работает с различными типами таблиц, подстраивая столбцы и кнопки управления в зависимости от выбранной таблицы.
 */
public class UniversalTableTabController implements Initializable {
    // Таблица для отображения данных
    public TableView<List<String>> tableView;

    // База данных для взаимодействия с таблицами
    private Database database;

    // Имя выбранной таблицы для отображения
    private String selectedTable;

    // Список имен столбцов таблицы
    private List<String> columnNames;

    // Кнопка добавления новой записи
    public Button addNewBtn;

    /**
     * Конструктор для универсального контроллера таблиц.
     *
     * @param table Название выбранной таблицы для управления.
     */
    public UniversalTableTabController(String table) {
        this.selectedTable = table;
    }

    /**
     * Обновляет таблицу, перезаполняя ее из базы данных.
     */
    public void updateTable() {
        cleaningTable();
        fillingTable();
        fillingColumnsTable();
    }

    /**
     * Инициализирует контроллер после загрузки элементов управления.
     * Загружает данные выбранной таблицы и настраивает элементы управления в соответствии с типом таблицы.
     *
     * @param location  URL для загрузки ресурсов (не используется).
     * @param resources Набор ресурсов для локализации (не используется).
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        if (!selectedTable.isEmpty()) {
            updateTable();
            // Настройка кнопки добавления в зависимости от типа таблицы
            if (selectedTable.equals("members")) {
                addNewBtn.setText("Добавить сотрудника");
            } else if (selectedTable.equals("orders")) {
                addNewBtn.setText("Добавить заказ");
            }
        }
    }

    /**
     * Очищает все элементы и столбцы таблицы перед обновлением.
     */
    private void cleaningTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
    }

    /**
     * Заполняет таблицу данными из выбранной таблицы базы данных.
     * Колонки идентифицируются по их именам из метаданных.
     */
    private void fillingTable() {
        ResultSet resultSet = database.getTable(selectedTable, "id");
        try {
            if (resultSet != null) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                columnNames = new ArrayList<>();

                // Заполняет список имен столбцов
                for (int i = 0; i < resultSetMetaData.getColumnCount(); ++i) {
                    columnNames.add(resultSetMetaData.getColumnName(i + 1));
                }

                ObservableList<List<String>> data = FXCollections.observableArrayList();

                // Добавляет каждую строку из результата запроса в таблицу
                while (resultSet.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    data.add(row);
                }

                tableView.setItems(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Создает столбцы таблицы с соответствующими именами и настраивает их для отображения данных.
     */
    private void fillingColumnsTable() {
        tableView.getColumns().clear();

        for (int i = 0; i < columnNames.size(); ++i) {
            TableColumn<List<String>, String> column = new TableColumn<>(columnNames.get(i));
            final int finalI = i;

            // Заполняет данные столбца из соответствующих строк таблицы
            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<List<String>, String> data) {
                    return new ReadOnlyStringWrapper(data.getValue().get(finalI));
                }
            });

            // Настраивает ячейки столбца для редактирования текста
            column.setCellFactory(TextFieldTableCell.forTableColumn());

            // Включает сортировку для столбца
            column.setSortable(true);

            tableView.getColumns().add(column);
        }

        // Активирует сортировку на уровне таблицы
        tableView.sort();
    }

    /**
     * Удаляет выбранную запись из таблицы и базы данных.
     * При удалении обновляет таблицу и отображает информационное сообщение.
     */
    public void onActionDelete() {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String columnSearch = ((List<?>) tableView.getItems().get(selectedIndex)).get(0).toString();
            String columnSearchName = ((TableColumn<?, ?>) tableView.getColumns().get(0)).getText();
            // Удаление записи из базы данных
            database.deleteQuery(selectedTable, columnSearchName, columnSearch);
            // Обновление таблицы
            tableView.getItems().remove(selectedIndex);
            updateTable();
            MyAlert.showInfoAlert("Запись успешно удалена");
        }
    }

    /**
     * Открывает диалог для добавления новой записи в таблицу и базу данных.
     * После добавления обновляет таблицу.
     */
    public void onActionAdd() {
        ArrayList<String> attrList = database.getAllTableColumnNames(selectedTable);
        attrList.remove(0);
        new UniversalAddDialog(selectedTable, attrList);
        updateTable();
    }
}
