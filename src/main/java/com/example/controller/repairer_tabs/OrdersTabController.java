package com.example.controller.repairer_tabs;

import com.example.bdclient.Database;
import com.example.controller.dialogs.DialogAddMembersController;
import com.example.controller.dialogs.DialogAddOrderController;
import com.example.controller.dialogs.MyAlert;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersTabController implements Initializable {
    public TableView tableView;
    public Label lblLogin;
    private Database database;
    private String selectedTable = "orders";
    private List<String> columnNames;
    public Button addNewBtn;

    public void updateTable() {
        cleaningTable();
        fillingTable();
        fillingColumnsTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        if (!selectedTable.isEmpty()) {
            updateTable();
        }
    }

    private void cleaningTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
    }

    private void fillingTable() {
        ResultSet resultSet = database.getTable(selectedTable, "id");
        try {
            if (resultSet != null) {
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                columnNames = new ArrayList<>();
                for (int i = 0; i < resultSetMetaData.getColumnCount(); ++i)
                    columnNames.add(resultSetMetaData.getColumnName(i + 1));
                ObservableList<List<String>> data = FXCollections.observableArrayList();
                while (resultSet.next()) {
                    List<String> row = new ArrayList<>();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
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

    private void fillingColumnsTable() {
        // Очистите существующие столбцы, если они есть
        tableView.getColumns().clear();

        for (int i = 0; i < columnNames.size(); ++i) {
            TableColumn column = new TableColumn(columnNames.get(i));
            final int finalI = i;

            column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<String>, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<List<String>, String> data) {
                    return new ReadOnlyStringWrapper(data.getValue().get(finalI));
                }
            });

            column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                public void handle(TableColumn.CellEditEvent event) {
                    TablePosition tablePosition = event.getTablePosition();
                    String columnSearch = ((List) tableView.getItems().get(tablePosition.getRow())).get(0).toString();
                    String columnSearchName = ((TableColumn) tableView.getColumns().get(0)).getText();
                    if (!database.updateTable(selectedTable, event.getTableColumn().getText(), event.getNewValue().toString(), columnSearchName, columnSearch)) {
                        new Alert(Alert.AlertType.WARNING, "Данные не изменены.").showAndWait();
                    }
                }
            });

            column.setCellFactory(TextFieldTableCell.forTableColumn());

            // Включите сортировку для столбца
            column.setSortable(true);

            tableView.getColumns().add(column);
        }

        // Сортируйте TableView
        tableView.sort();
    }


    // TODO: сделать провеку перед удалением
    public void onActionDelete(ActionEvent actionEvent) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String columnSearch = ((List) tableView.getItems().get(selectedIndex)).get(0).toString();
            String columnSearchName = ((TableColumn) tableView.getColumns().get(0)).getText();
            tableView.getItems().remove(selectedIndex);
            database.deleteRowTable(selectedTable, columnSearchName, columnSearch);
            updateTable();
            MyAlert.showInfoAlert("Запись успешно удалена");
        }
    }

    public void onActionAdd(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/DialogAddOrder.fxml"));
            DialogAddOrderController dialogAddOrderController = new DialogAddOrderController();
            loader.setController(dialogAddOrderController);

            // Передача текущего контроллера как userData
            Stage stage = new Stage();
            stage.setTitle("Добавление заказа");
            stage.setScene(new Scene(loader.load()));
            stage.setUserData(this); // передаем UsersTabController

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

