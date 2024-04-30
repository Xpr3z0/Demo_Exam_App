package com.example.controller.dialogs;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.example.bdclient.Database;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UniversalFormDialog {

    private List<String> fields;
    private Database database;
    private String table;

    public UniversalFormDialog(String table, ArrayList<String> fields) {
        database = Database.getInstance();
        this.table = table;
        this.fields = fields;
        showDialog();
    }

    public void showDialog() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        BorderPane root = new BorderPane();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        for (int i = 0; i < fields.size(); i++) {
            String fieldName = fields.get(i);

            Label label = new Label(fieldName);
            TextField textField = new TextField();

            gridPane.add(label, 0, i);
            gridPane.add(textField, 1, i);
        }

        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(10);
        buttonsBox.setPadding(new Insets(20));

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> {
            saveData(gridPane);
            stage.close();
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.setOnAction(event -> stage.close());

        buttonsBox.getChildren().addAll(addButton, cancelButton);

        root.setCenter(gridPane);
        root.setBottom(buttonsBox);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private void saveData(GridPane gridPane) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");

        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(fields.get(i));
        }

        sql.append(") VALUES (");

        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            String value = ((TextField) gridPane.getChildren().get(i * 2 + 1)).getText();
            sql.append("'").append(value).append("'");
        }

        sql.append(")");

        database.simpleQuery(sql.toString());
        MyAlert.showInfoAlert("Запись добавлена успешно");

    }
}
