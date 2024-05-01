package com.example.controller.manager_tabs;

import com.example.util.Database;
import com.example.util.UniversalFormDialog;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UsersTabController implements Initializable {
    public TableView tableView;
    private Database database;
    private String selectedTable = "members";
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

                for (int i = 0; i < resultSetMetaData.getColumnCount(); ++i) {
                    columnNames.add(resultSetMetaData.getColumnName(i + 1));
                }

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
            database.deleteQuery(selectedTable, columnSearchName, columnSearch);
            updateTable();
        }
    }

    public void onActionAdd() {
        ArrayList<String> attrList = database.getAllTableColumnNames("members");
        attrList.remove(0);
        new UniversalFormDialog("members", attrList);
        updateTable();
    }
}

