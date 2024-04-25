package com.example.controller.dialogs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.BDController;
import com.example.controller.manager_tabs.UsersTabController;
import com.example.controller.repairer_tabs.ResponsibleRequestsTabController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DialogAddReportController implements Initializable {
//    public VBox valuesVbox;
    public Button idBottomAdd;
    public TextField requestNumTF;
    private String selectedTable = "reports";
//    public ChoiceBox<String> roleChoice;
    public GridPane gridPane;
    private int requestNum;


    public DialogAddReportController(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    public DialogAddReportController(int requestNum) {
        this.requestNum = requestNum;
    }


    public void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
//        showTable();
    }


    public void onActionBottomAdd(ActionEvent actionEvent) {
//        ObservableList<Node> list = valuesVbox.getChildren();
        ObservableList<Node> list = gridPane.getChildren();
        String sqlAdd = "INSERT INTO reports (request_id, repair_type, time, cost, resources, reason, help) VALUES (";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TextField) {
                sqlAdd += "'" + ((TextField) list.get(i)).getText();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";
            } else if (list.get(i) instanceof TextArea) {
                sqlAdd += "'" + ((TextArea) list.get(i)).getText();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";
            }
        }
        sqlAdd += "');";

        boolean success = ClientPostgreSQL.getInstance().simpleQuery(selectedTable, sqlAdd);
        if (success) {
            MyAlert.showInfoAlert("Отчёт успешно создан");
        } else {
            MyAlert.showErrorAlert("Произошла ошибка при создании отчёта");
        }

        // Получаем текущее окно и закрываем его
        Stage stage = (Stage) idBottomAdd.getScene().getWindow();
        stage.close();

        // Обновляем таблицу в UsersTabController
        ResponsibleRequestsTabController parentController = (ResponsibleRequestsTabController) stage.getUserData();
        parentController.showMoreInfo(requestNum);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        requestNumTF.setText(String.valueOf(requestNum));
//        roleChoice.getItems().addAll("operator", "manager", "repairer");
        // ...
    }
}
