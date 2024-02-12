package com.example.controller.dialogs;

import com.example.controller.BDController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.bdclient.ClientPostgreSQL;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DialogAddDrugsController implements Initializable {
    @FXML
    public ChoiceBox<String> measureUnitChoice;
    public VBox valuesVbox;
    public Button idBottomAdd;
    private String selectedTable;


    public DialogAddDrugsController(String selectedTable) {
        this.selectedTable = selectedTable;
    }


    public void onCancelBtn(ActionEvent actionEvent) {
        showTable();
    }


    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlAdd = "INSERT INTO Drugs (" +
                "name, manufacturer, packaging_quantity, measure_unit, dosage_mg, in_price, out_price, in_stock) VALUES (";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TextField) {

                sqlAdd += "'" + ((TextField) list.get(i)).getText();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";

            } else if (list.get(i) instanceof HBox) {
                ObservableList<Node> childList = ((HBox) list.get(i)).getChildren();
                for (Node node : childList) {
                    if (node instanceof TextField) {
                        sqlAdd += "'" + ((TextField) node).getText();
                    } else if (node instanceof ChoiceBox) {
                        sqlAdd += "'" + ((ChoiceBox<?>) node).getValue();
                    }
                    sqlAdd += (childList.indexOf(node) + 1 == childList.size() && i + 1 == list.size()) ? "" : "',";
                }
            }
        }
        sqlAdd += "');";

        ClientPostgreSQL.getInstance().simpleQuery(selectedTable, sqlAdd);
        showTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        measureUnitChoice.getItems().addAll("таб", "капс", "г", "мл");
    }




    public void showTable() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BD.fxml"));
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
