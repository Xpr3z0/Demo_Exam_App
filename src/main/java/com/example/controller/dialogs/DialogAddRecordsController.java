package com.example.controller.dialogs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.BDController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.TextFields;

public class DialogAddRecordsController implements Initializable {
//    @FXML
//    private ChoiceBox<String> drugChoiceBox;
    @FXML
    private TextField drugNameTF;
    @FXML
    private TextField packagingQuantityTextField;
    @FXML
    private TextField totalTextField;
    @FXML
    public Label warnLabel;
    @FXML
    private RadioButton sellRB;
    @FXML
    private RadioButton buyRB;
    public Button idBottomAdd;
    private Connection connection = null;
    private String selectedTable;
    private ClientPostgreSQL clientPostgreSQL;
    private boolean availableInStock = true;
    private boolean drugExists = false;
    private int requiredQuantity;
    private int in_stock;

    public DialogAddRecordsController(String selectedTable) {
        this.selectedTable = selectedTable;
        clientPostgreSQL = ClientPostgreSQL.getInstance();
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        warnLabel.setVisible(false);

        ArrayList<String> resultList = clientPostgreSQL.stringListQuery("SELECT name FROM drugs ORDER BY drug_id");
        TextFields.bindAutoCompletion(drugNameTF, resultList);

        buyRB.setOnAction(event -> calculateTotal());
        sellRB.setOnAction(event -> calculateTotal());
        packagingQuantityTextField.textProperty().addListener((observable, oldValue, newValue) -> calculateTotal());

        drugNameTF.textProperty().addListener((observable, oldValue, newValue) -> {
            drugExists = false;
            for (String name : resultList) {
                if (name.equals(drugNameTF.getText().trim())) {
                    drugExists = true;
                    calculateTotal();
                    break;
                } else {
                    totalTextField.setText("0.00");
                }
            }
        });
    }

    @FXML
    private void calculateTotal() {
//        String selectedDrug = drugChoiceBox.getValue();
        String selectedDrug = drugNameTF.getText().trim();
        String quantityText = packagingQuantityTextField.getText();
        availableInStock = true;
        warnLabel.setVisible(false);
        if (selectedDrug != null && !quantityText.isEmpty()) {
            try {
                connection = clientPostgreSQL.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT in_price, out_price, in_stock FROM drugs WHERE name = ?");
                preparedStatement.setString(1, selectedDrug);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double outPrice = resultSet.getDouble("out_price");
                    double inPrice = resultSet.getDouble("in_price");
                    in_stock = resultSet.getInt("in_stock");
                    requiredQuantity = Integer.parseInt(quantityText);
                    double total;

                    if (requiredQuantity < 1) {
                        warnLabel.setVisible(true);
                    } else {

                        if (buyRB.isSelected()) {
                            total = inPrice * requiredQuantity * -1;
                        } else {
                            total = outPrice * requiredQuantity;
                            if (requiredQuantity > in_stock) {
                                availableInStock = false;
                            }
                            requiredQuantity *= -1;
                        }

                        totalTextField.setText(String.format(Locale.US, "%.2f", total));
                    }
                }
                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException exception) {
                warnLabel.setVisible(true);
            } finally {
                try {
                    clientPostgreSQL.closeConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            totalTextField.setText("0.00");
        }
    }

    public void onCancelBtn(ActionEvent actionEvent) {
        showTable();
    }
    @FXML
    private void onActionBottomAdd(ActionEvent event) {
        String selectedDrug = drugNameTF.getText().trim();
        String quantityText = packagingQuantityTextField.getText();
        String totalText = totalTextField.getText();
        if (drugExists) {
            if (availableInStock) {
                if (!quantityText.isEmpty() && !totalText.isEmpty()) {
                    try {
                        connection = clientPostgreSQL.getConnection();
                        int quantity = Integer.parseInt(quantityText);
                        double total = Double.parseDouble(totalText);
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = now.format(formatter);

                        PreparedStatement preparedStatement = connection.prepareStatement(
                                "INSERT INTO records (drug, packaging_quantity, total, transaction_datetime) VALUES (?, ?, ?, ?)");
                        preparedStatement.setString(1, selectedDrug);
                        preparedStatement.setInt(2, quantity);
                        preparedStatement.setDouble(3, total);
                        preparedStatement.setTimestamp(4, Timestamp.valueOf(formattedDateTime));
                        String resultStr = preparedStatement.toString();
                        ClientPostgreSQL.getInstance().simpleQuery(selectedTable, resultStr);
                        int newStock = in_stock + requiredQuantity;
                        String updateQuery = "UPDATE drugs SET in_stock = " + newStock + " WHERE name = '" + selectedDrug + "'";
                        ClientPostgreSQL.getInstance().simpleQuery(selectedTable, updateQuery);
                        preparedStatement.close();

                        showInfoAlert("Запись добавлена успешно.");
                        showTable();

                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                        showErrorAlert("Ошибка при добавлении записи");
                    } finally {
                        try {
                            clientPostgreSQL.closeConnection();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    showErrorAlert("Пожалуйста, заполните все поля.");
                }
            } else {
                showErrorAlert("Недостаточно единиц товара на складе.");
            }
        } else {
            showErrorAlert("Указанный препарат отсутствует в базе.");
        }
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

