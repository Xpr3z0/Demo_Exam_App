package com.example.controller.dialogs;

import com.example.bdclient.Database;
import com.example.controller.manager_tabs.UsersTabController;
import com.example.controller.repairer_tabs.OrdersTabController;
import com.example.controller.repairer_tabs.ResponsibleRequestsTabController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class DialogAddOrderController implements Initializable {
    public GridPane gridPane;

    public DialogAddOrderController() {

    }

    @FXML
    private void onCancelBtn(ActionEvent actionEvent) {
        ((Stage) gridPane.getScene().getWindow()).close();
    }


    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = gridPane.getChildren();
        String sqlAdd = "INSERT INTO orders (request_id, resource_type, resource_name, cost) VALUES (";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TextField) {
                sqlAdd += "'" + ((TextField) list.get(i)).getText();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";
            }
        }
        sqlAdd += "');";

        boolean success = Database.getInstance().simpleQuery(sqlAdd);
        if (success) {
            MyAlert.showInfoAlert("Заказ добавлен успешно");
        } else {
            MyAlert.showErrorAlert("Произошла ошибка при добавлении заказа");
        }

        // Получаем текущее окно и закрываем его
        Stage stage = (Stage) gridPane.getScene().getWindow();
        stage.close();

        // Обновляем таблицу в UsersTabController
        OrdersTabController parentController = (OrdersTabController) stage.getUserData();
        parentController.updateTable();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
