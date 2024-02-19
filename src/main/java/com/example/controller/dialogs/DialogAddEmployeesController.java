package com.example.controller.dialogs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.BDController;
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


    public DialogAddEmployeesController(String selectedTable) {
        this.selectedTable = selectedTable;
    }


    public void onCancelBtn(ActionEvent actionEvent) {
        showTable();
    }


    public void onActionBottomAdd(ActionEvent actionEvent) {
        ObservableList<Node> list = valuesVbox.getChildren();
        String sqlAdd = "INSERT INTO Employees (last_name, first_name, patronymic, phone) VALUES (";

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof TextField) {

                sqlAdd += "'" + ((TextField) list.get(i)).getText();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";

            }
            else if (list.get(i) instanceof ChoiceBox){

                sqlAdd += "'" + ((ChoiceBox) list.get(i)).getValue();
                sqlAdd += (i + 1 != list.size()) ? "'," : "";

            }
        }
        sqlAdd += "');";

        ClientPostgreSQL.getInstance().simpleQuery(selectedTable, sqlAdd);
        showTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
