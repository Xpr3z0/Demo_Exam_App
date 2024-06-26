package com.example.controller.dialogs;

import com.example.bdclient.Database;
import com.example.controller.repairer_tabs.ResponsibleRequestsTabController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogAddReportController implements Initializable {
//    public VBox valuesVbox;
    public Button idBottomAdd;
    public TextField requestNumTF;
    public GridPane gridPane;
    private int requestNum;

    public DialogAddReportController(int requestNum) {
        this.requestNum = requestNum;
    }

    @FXML
    private void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) idBottomAdd.getScene().getWindow()).close();
    }


    public void onActionBottomAdd(ActionEvent actionEvent) {
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

        boolean success = Database.getInstance().simpleQuery(sqlAdd);
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
    }
}
