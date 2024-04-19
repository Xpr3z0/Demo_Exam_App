package com.example.controller.dialogs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.BDController;
import com.example.controller.manager_tabs.UsersTabController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DialogAddEmployeesController implements Initializable {
    public VBox valuesVbox;
    public Button idBottomAdd;
    private String selectedTable;
    public ChoiceBox<String> roleChoice;


    public DialogAddEmployeesController(String selectedTable) {
        this.selectedTable = selectedTable;
    }


    public void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
//        showTable();
    }


    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlAdd = "INSERT INTO Employees (name, login, pass, role) VALUES (";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TextField) {
                sqlAdd += "'" + ((TextField) list.get(i)).getText();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";
            } else if (list.get(i) instanceof ChoiceBox) {
                sqlAdd += "'" + ((ChoiceBox) list.get(i)).getValue();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";
            }
        }
        sqlAdd += "');";

        boolean success = ClientPostgreSQL.getInstance().simpleQuery(selectedTable, sqlAdd);
        if (success) {
            MyAlert.showInfoAlert("Запись добавлена успешно");
        } else {
            MyAlert.showErrorAlert("Произошла ошибка при добавлении записи");
        }

        // Получаем текущее окно и закрываем его
        Stage stage = (Stage) idBottomAdd.getScene().getWindow();
        stage.close();

        // Обновляем таблицу в UsersTabController
        UsersTabController parentController = (UsersTabController) stage.getUserData();
        parentController.updateTable();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleChoice.getItems().addAll("operator", "manager", "repairer");
        // ...
    }

    private void showTable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dialogs/BD.fxml"));
            BDController dialogAddController = new BDController(selectedTable);
            loader.setController(dialogAddController);
            Stage stage = new Stage();
            stage.setTitle("Таблица");
            stage.setScene(new Scene(loader.load()));
            stage.show();
            ((Stage) idBottomAdd.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
