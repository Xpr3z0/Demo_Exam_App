package com.example.util;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Класс UniversalAddDialog обеспечивает универсальное диалоговое окно для добавления записей в базу данных.
 */
public class UniversalAddDialog {

    private List<String> fields;
    private Database database;
    private String table;

    /**
     * Конструктор, который принимает имя таблицы и список полей для добавления новой записи.
     *
     * @param table  имя таблицы, в которую будет добавлена запись.
     * @param fields список названий полей, которые будут добавлены.
     */
    public UniversalAddDialog(String table, ArrayList<String> fields) {
        database = Database.getInstance();
        this.table = table;
        this.fields = fields;
        showDialog();
    }

    /**
     * Отображает диалоговое окно для добавления новой записи в базу данных.
     */
    public void showDialog() {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Добавление записи");

        BorderPane root = new BorderPane();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        // Добавление меток и текстовых полей для каждого из полей таблицы.
        for (int i = 0; i < fields.size(); i++) {
            String fieldName = fields.get(i);

            Label label = new Label(fieldName);
            TextField textField = new TextField();

            gridPane.add(label, 0, i);
            gridPane.add(textField, 1, i);
        }

        // Создание контейнера для кнопок
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(10);
        buttonsBox.setPadding(new Insets(20));

        // Кнопка добавления, которая вызывает сохранение данных и закрывает окно
        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> {
            saveData(gridPane);
            stage.close();
        });

        // Кнопка отмены, которая просто закрывает окно
        Button cancelButton = new Button("Отмена");
        cancelButton.setOnAction(event -> stage.close());

        buttonsBox.getChildren().addAll(addButton, cancelButton);

        root.setCenter(gridPane);
        root.setBottom(buttonsBox);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.showAndWait();
    }

    /**
     * Сохраняет введенные данные в базу данных, формируя SQL-запрос на основе введенных значений.
     *
     * @param gridPane контейнер с текстовыми полями, содержащими значения для записи в базу данных.
     */
    private void saveData(GridPane gridPane) {
        StringBuilder sql = new StringBuilder("INSERT INTO " + table + " (");

        // Формирование SQL-запроса с именами полей
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append(fields.get(i));
        }

        sql.append(") VALUES (");

        // Добавление значений из текстовых полей в запрос
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            String value = ((TextField) gridPane.getChildren().get(i * 2 + 1)).getText();
            sql.append("'").append(value).append("'");
        }

        sql.append(")");

        // Выполнение запроса в базе данных
        database.simpleQuery(sql.toString());
        MyAlert.showInfoAlert("Запись добавлена успешно");
    }
}
