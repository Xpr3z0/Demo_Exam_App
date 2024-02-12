package com.example.controller;

import com.example.controller.dialogs.DialogAddDrugsController;
import com.example.controller.dialogs.DialogAddEmployeesController;
import com.example.controller.dialogs.DialogAddRecordsController;
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
import com.example.bdclient.ClientPostgreSQL;
import com.example.bdclient.JDBCClient;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BDController implements Initializable {
    public ChoiceBox<String> tableChoice;
    public TableView tableView;
    public Label lblLogin;
    private JDBCClient jdbcClient;
    private String selectedTable = "";
    private List<String> columnNames;
    public Button addNewBtn;

    BDController() {}

    public BDController(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    private void updateTable() {
        cleaningTable();
        fillingTable();
        fillingColumnsTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        jdbcClient = ClientPostgreSQL.getInstance();
        lblLogin.setText(jdbcClient.getLogin());
        List<String> nameTablesSet = jdbcClient.getTableNames();
        if (!selectedTable.isEmpty()) {
            updateTable();
        }
        if (nameTablesSet != null) {
            tableChoice.getItems().addAll(jdbcClient.getTableNames());
            for (int i = 0; i < tableChoice.getItems().size(); i++) {
                tableChoice.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        selectedTable = tableChoice.getValue();
                        updateTable();
                    }
                });
            }
            if ((selectedTable == null || selectedTable.trim().equals(""))) {
                tableChoice.setValue(tableChoice.getItems().get(0));
            } else {
                tableChoice.setValue(selectedTable);
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "Таблицы не найдены!").showAndWait();
        }
    }

    private void cleaningTable() {
        tableView.getColumns().clear();
        tableView.getItems().clear();
    }

    private void fillingTable() {
        ResultSet resultSet = jdbcClient.getTable(selectedTable);
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
                tableChoice.setValue(selectedTable);
                switch (selectedTable) {
                    case "drugs": addNewBtn.setText("Добавить новый препарат"); break;
                    case "employees": addNewBtn.setText("Добавить нового сотрудника"); break;
                    case "records": addNewBtn.setText("Добавить новую запись"); break;
                    default: addNewBtn.setText("Добавить");
                }

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
                    if (!jdbcClient.updateTable(selectedTable, event.getTableColumn().getText(), event.getNewValue().toString(), columnSearchName, columnSearch)) {
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



    public void onActionDelete(ActionEvent actionEvent) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String columnSearch = ((List) tableView.getItems().get(selectedIndex)).get(0).toString();
            String columnSearchName = ((TableColumn) tableView.getColumns().get(0)).getText();
            tableView.getItems().remove(selectedIndex);
            jdbcClient.deleteRowTable(selectedTable, columnSearchName, columnSearch);
        }
    }

    public void onActionAdd(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            if (selectedTable.equals("drugs")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/DialogAddDrugs.fxml"));
                DialogAddDrugsController dialogAddDrugsController = new DialogAddDrugsController(selectedTable);
                loader.setController(dialogAddDrugsController);
                stage.setTitle("Добавление нового препарата");
                stage.setScene(new Scene(loader.load()));

            } else if (selectedTable.equals("employees")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/DialogAddEmployees.fxml"));
                DialogAddEmployeesController dialogAddEmployeesController = new DialogAddEmployeesController(selectedTable);
                loader.setController(dialogAddEmployeesController);
                stage.setTitle("Добавление нового сотрудника");
                stage.setScene(new Scene(loader.load()));

            } else if (selectedTable.equals("records")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/DialogAddRecords.fxml"));
                DialogAddRecordsController dialogAddRecordsController = new DialogAddRecordsController(selectedTable);
                loader.setController(dialogAddRecordsController);
                stage.setTitle("Добавление записи");
                stage.setScene(new Scene(loader.load()));

            }

            stage.show();
            Stage stageClose = (Stage) tableView.getScene().getWindow();
            stageClose.hide();

            stage.setOnCloseRequest(event -> {
                stageClose.show();
                stage.close();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
